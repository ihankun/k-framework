package io.hankun.framework.ai.agent.audio.player;

import io.hankun.framework.ai.agent.audio.KAudioData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import javax.sound.sampled.*;
import java.util.Base64;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@Slf4j
public class AudioPlayer {

    // 音频数据队列，用于存储流式音频数据
    private final BlockingQueue<byte[]> audioQueue = new LinkedBlockingQueue<>();

    // 播放控制标志
    private volatile boolean playing = false;
    private volatile boolean running = true;

    // 音频播放线程
    private Thread playThread;

    // Audio资源
    private SourceDataLine sourceDataLine;

    private final AudioFormat audioFormat;

    /**
     * 判断当前设备是否支持音频播放
     *
     * @return true表示支持，false表示不支持
     */
    public static boolean isSupportAudio() {
        return AudioSystem.getMixerInfo().length > 0;
    }


    public AudioPlayer() {
        this.audioFormat = new AudioFormat(24000, 16, 1, true, false);
        initAudioLine();
    }

    public AudioPlayer(AudioFormat audioFormat) {
        this.audioFormat = audioFormat;
        initAudioLine();
    }

    /**
     * 初始化音频输出线路
     */
    private void initAudioLine() {
        try {
            DataLine.Info info = new DataLine.Info(SourceDataLine.class, audioFormat);
            sourceDataLine = (SourceDataLine) AudioSystem.getLine(info);
            sourceDataLine.open(audioFormat);
        } catch (LineUnavailableException e) {
            log.error("Failed to get audio line: e=", e);
        }
    }

    public void addData(KAudioData kAudioData) {
        String audio = kAudioData.audio();
        if (StringUtils.hasText(audio)) {
            addData(Base64.getDecoder().decode(audio));
        }
    }

    /**
     * 添加音频数据到播放队列
     *
     * @param data 音频字节数据
     */
    public void addData(byte[] data) {
        if (data != null && data.length > 0) {
            try {
                audioQueue.put(data);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log.error("Failed to put audio data into queue: e=", e);
            }
        }
    }

    /**
     * 开始播放音频流
     */
    public void play() {
        if (playing) return;

        playing = true;
        if (playThread == null || !playThread.isAlive()) {
            playThread = new Thread(this::playbackLoop);
            playThread.setDaemon(true);
            playThread.start();
        }
        sourceDataLine.start();
    }

    /**
     * 暂停播放
     */
    public void pause() {
        playing = false;
        if (sourceDataLine != null) {
            sourceDataLine.stop();
        }
    }

    /**
     * 停止播放并清空队列
     */
    public void stop() {
        playing = false;
        running = false;

        // 清空队列
        audioQueue.clear();

        if (sourceDataLine != null) {
            sourceDataLine.stop();
            sourceDataLine.flush();
        }

        // 中断播放线程
        if (playThread != null && playThread.isAlive()) {
            playThread.interrupt();
        }
    }

    /**
     * 播放循环
     */
    private void playbackLoop() {
        try {
            while (running) {
                if (playing) {
                    byte[] data = audioQueue.take(); // 阻塞等待数据
                    if (data.length > 0) {
                        sourceDataLine.write(data, 0, data.length);
                    }
                } else {
                    Thread.sleep(10); // 短暂休眠避免CPU空转
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (Exception e) {
            log.error("Failed to play audio: e=", e);
        }
    }

    /**
     * 获取队列中等待播放的数据大小
     *
     * @return 队列大小
     */
    public int getQueueSize() {
        return audioQueue.size();
    }

    /**
     * 释放音频资源
     */
    public void release() {
        stop();
        if (sourceDataLine != null) {
            sourceDataLine.close();
        }
    }

    public void waitFinish() {
        // 等待音频数据播放完毕
        sourceDataLine.drain();
        release();
    }
}
