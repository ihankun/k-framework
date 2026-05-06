package io.hankun.framework.ai.agent.audio.controller;

import io.hankun.framework.ai.agent.audio.AudioOutputSaveService;
import io.hankun.framework.ai.agent.audio.KAudioData;
import io.hankun.framework.ai.agent.audio.KAudioService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.nio.ByteBuffer;
import java.util.Base64;
import java.util.function.Function;

/**
 * @description:
 * @className: AudioController
 * @createAt: 2025/7/31 10:32
 * @author: hankun
 */
@Slf4j
@RestController
@RequestMapping("/audio")
public class AudioController {

    private final KAudioService kAudioService;

    private final AudioOutputSaveService audioOutputSaveService;

    public AudioController(KAudioService kAudioService, AudioOutputSaveService audioOutputSaveService) {
        this.kAudioService = kAudioService;
        this.audioOutputSaveService = audioOutputSaveService;
    }

    /**
     * 文本转语音文件
     *
     * @param text 文本
     */
    @GetMapping("/ttsToFile")
    public ResponseEntity<byte[]> ttsToFile(@RequestParam(value = "text") String text) {
        Flux<String> textFlux = Flux.just(text);
        Flux<KAudioData> audioDataFlux = kAudioService.tts("", textFlux);
        Flux<byte[]> byteFlux = audioDataFlux.filter(audioData -> StringUtils.hasText(audioData.audio()))
                .map(audioData -> Base64.getDecoder().decode(audioData.audio()));
        byteFlux = audioOutputSaveService.convertStream(byteFlux);
        //合并所以的byte[]
        byte[] bytes = byteFlux.collectList()
                .map(list -> {
                    int totalLength = list.stream().mapToInt(arr -> arr.length).sum();
                    ByteBuffer buffer = ByteBuffer.allocate(totalLength);
                    list.forEach(buffer::put);
                    return buffer.array();
                })
                .block();
        if (bytes == null) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", "data.wav");
        headers.setContentLength(bytes.length);

        return new ResponseEntity<>(bytes, headers, HttpStatus.OK);
    }

    @GetMapping(value = "/ttsToBase64", produces = "text/event-stream;charset=UTF-8")
    public Flux<ServerSentEvent<KAudioData>> ttsToBase64(@RequestParam(value = "text") String text) {
        Flux<String> textFlux = Flux.just(text);
        Flux<KAudioData> audioDataFlux = kAudioService.tts("", textFlux);
        return audioDataFlux.map(new Function<KAudioData, ServerSentEvent<KAudioData>>() {
            public ServerSentEvent<KAudioData> apply(KAudioData s) {
                int length = 0;
                if (StringUtils.hasText(s.audio())) {
                    length = s.audio().length();
                }
                log.debug("返回用户语音:index={},text={},type={},audioLength={}", s.index(), s.text(), s.type(), length);
                return ServerSentEvent.<KAudioData>builder()
                        .id(String.valueOf(System.currentTimeMillis()))
                        .data(s)
                        .build();
            }
        });
    }
}
