package io.hankun.framework.ai.agent.audio;

import io.hankun.framework.ai.agent.audio.config.KAudioConfig;
import io.hankun.framework.ai.agent.audio.tts.entity.KAudioFormat;
import io.hankun.framework.ai.agent.audio.tts.entity.TtsType;
import io.hankun.framework.ai.agent.util.FileUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Flux;

import javax.sound.sampled.AudioFormat;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Base64;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @description:
 * @className: AudioOutputSaveService
 * @createAt: 2025/8/5 13:42
 * @author: hankun
 */
@Slf4j
@Component
public class AudioOutputSaveService {

    public static final int WAV_HEADER = 44;
    private final KAudioConfig kAudioConfig;

    private final KAudioService audioService;

    public AudioOutputSaveService(KAudioConfig kAudioConfig,
                                  KAudioService audioService) {
        this.kAudioConfig = kAudioConfig;
        this.audioService = audioService;
        Path path = getBasePath();
        // 确保目录存在
        try {
            Files.createDirectories(path);
        } catch (IOException e) {
            // 处理目录创建失败的情况
            log.error("Failed to create directory: {}", path, e);
            // 可以选择返回错误或使用其他目录
        }
    }

    private static Path getBasePath() {
        return Paths.get(FileUtil.BASE, "audio");
    }

    private String getFileContent() {
        if (TtsType.ALI.getCode().equals(kAudioConfig.getType())) {
            String format = kAudioConfig.getAli().getFormat();
            if (StringUtils.hasText(format)) {
                return format;
            }
            return "wav";
        }
        return "wav";
    }

    private static String convertMessage(String message) {
        if (ObjectUtils.isEmpty(message)) {
            return "";
        }
        //保留5个字符
        return message.substring(0, Math.min(message.length(), 5));
    }

    private Path getAudioPath(String conversationId, String message) {
        Path path = getBasePath();
        String fileName = String.format("%s_%s_%s", conversationId, System.currentTimeMillis(), convertMessage(message));
        return Paths.get(path.toString(), FileUtil.validateFileName(fileName) + "." + getFileContent());
    }

    public void save(Flux<KAudioData> adioStream, String conversationId, String message) {
        if (!kAudioConfig.getSaveAudio()) {
            return;
        }
        Flux<byte[]> byteFlux = adioStream.filter(audioData -> StringUtils.hasText(audioData.audio()))
                .map(audioData -> Base64.getDecoder().decode(audioData.audio()));
        saveBytes(byteFlux, conversationId, message);
    }

    public void saveBytes(Flux<byte[]> adioStream, String conversationId, String message) {
        KAudioFormat kAudioFormat = audioService.buildAudioFormat();
        Flux<byte[]> byteFlux = convertStream(kAudioFormat, adioStream);
        saveWav(kAudioFormat.audioFormat(), byteFlux, conversationId, message);
    }

    public Flux<byte[]> convertStream(Flux<byte[]> audioStream) {
        KAudioFormat kAudioFormat = audioService.buildAudioFormat();
        return convertStream(kAudioFormat, audioStream);
    }

    private Flux<byte[]> convertStream(KAudioFormat kAudioFormat, Flux<byte[]> audioStream) {
        switch (kAudioFormat.format()) {
            case "wav" -> {
                return audioStream;
            }
            case "pcm" -> {
                return pcmUpdate(kAudioFormat.audioFormat(), audioStream);
            }
            default -> {
                log.error("不支持的音频格式：{}", kAudioFormat.format());
                return Flux.empty();
            }
        }
    }

    private Flux<byte[]> pcmUpdate(AudioFormat audioFormat, Flux<byte[]> audioStream) {
        byte[] wavHeader = createWavHeader(audioFormat, -1);
        return Flux.just(wavHeader).concatWith(audioStream);
    }

    private void saveWav(AudioFormat audioFormat, Flux<byte[]> adioStream, String conversationId, String message) {
        Path path = getAudioPath(conversationId, message);
        AtomicLong totalDataLength = new AtomicLong(0L);
        DataBufferUtils.write(
                adioStream.map(bytes -> {
                    totalDataLength.addAndGet(bytes.length);
                    return new DefaultDataBufferFactory().wrap(bytes);
                }),
                path,
                StandardOpenOption.CREATE,
                StandardOpenOption.WRITE,
                StandardOpenOption.TRUNCATE_EXISTING  // 确保文件被清空重写
        ).doOnNext((data) -> {
            updateWavHeader(path, audioFormat, totalDataLength.get() - WAV_HEADER);
        }).doOnError((error) -> {
            log.error("Failed to save audio: e=", error);
        }).subscribe();
    }


    /**
     * 创建WAV文件头部
     *
     * @param format     音频格式
     * @param dataLength 数据长度
     * @return WAV头部字节数组
     */
    private byte[] createWavHeader(AudioFormat format, long dataLength) {
        int byteRate = (int) (format.getSampleRate() * format.getChannels() * (format.getSampleSizeInBits() / 8));
        int blockAlign = format.getChannels() * (format.getSampleSizeInBits() / 8);

        byte[] header = new byte[WAV_HEADER];
        // RIFF header
        System.arraycopy(new byte[]{'R', 'I', 'F', 'F'}, 0, header, 0, 4);
        // Chunk size (will be filled later)
        writeInt(header, 4, (int) (36 + dataLength));
        // WAVE header
        System.arraycopy(new byte[]{'W', 'A', 'V', 'E'}, 0, header, 8, 4);
        // fmt subchunk
        System.arraycopy(new byte[]{'f', 'm', 't', ' '}, 0, header, 12, 4);
        writeInt(header, 16, 16); // SubChunk1Size
        writeShort(header, 20, (short) 1); // AudioFormat (1 = PCM)
        writeShort(header, 22, (short) format.getChannels()); // NumChannels
        writeInt(header, 24, (int) format.getSampleRate()); // SampleRate
        writeInt(header, 28, byteRate); // ByteRate
        writeShort(header, 32, (short) blockAlign); // BlockAlign
        writeShort(header, 34, (short) format.getSampleSizeInBits()); // BitsPerSample
        // data subchunk
        System.arraycopy(new byte[]{'d', 'a', 't', 'a'}, 0, header, 36, 4);
        writeInt(header, 40, (int) dataLength); // SubChunk2Size

        return header;
    }

    /**
     * 更新WAV文件头部的数据长度信息
     *
     * @param path       文件路径
     * @param format     音频格式
     * @param dataLength 数据长度
     */
    private void updateWavHeader(Path path, AudioFormat format, long dataLength) {
        try {
            byte[] header = createWavHeader(format, dataLength);
            // 更新整个头部
            Files.write(path, header, StandardOpenOption.WRITE, StandardOpenOption.WRITE);
        } catch (IOException e) {
            log.error("更新WAV文件头部失败", e);
        }
    }

    /**
     * 在字节数组中写入一个32位整数（小端序）
     *
     * @param buffer 字节数组
     * @param offset 偏移量
     * @param value  要写入的值
     */
    private void writeInt(byte[] buffer, int offset, int value) {
        buffer[offset] = (byte) (value & 0xff);
        buffer[offset + 1] = (byte) ((value >> 8) & 0xff);
        buffer[offset + 2] = (byte) ((value >> 16) & 0xff);
        buffer[offset + 3] = (byte) ((value >> 24) & 0xff);
    }

    /**
     * 在字节数组中写入一个16位短整数（小端序）
     *
     * @param buffer 字节数组
     * @param offset 偏移量
     * @param value  要写入的值
     */
    private void writeShort(byte[] buffer, int offset, short value) {
        buffer[offset] = (byte) (value & 0xff);
        buffer[offset + 1] = (byte) ((value >> 8) & 0xff);
    }
}
