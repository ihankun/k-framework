package io.hankun.framework.ai.agent.audio;

import io.hankun.framework.ai.agent.audio.asr.entity.KAsrResult;
import io.hankun.framework.ai.agent.audio.tts.entity.KAudioFormat;
import io.hankun.framework.ai.agent.entity.DataWithMeta;
import io.hankun.framework.ai.agent.entity.NodeResultData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Flux;

import java.nio.ByteBuffer;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;

/**
 * @description:
 * @className: KMessageAudioService
 * @createAt: 2025/11/12 17:17
 * @author: hankun
 */
@Slf4j
@Component
public class KMessageAudioService {
    private final KAudioService audioService;

    private final AudioOutputSaveService audioOutputSaveService;


    public KMessageAudioService(KAudioService audioService,
                                AudioOutputSaveService audioOutputSaveService) {
        this.audioService = audioService;
        this.audioOutputSaveService = audioOutputSaveService;
    }

    public KAudioFormat buildAudioFormat() {
        return audioService.buildAudioFormat();
    }

    public Flux<ServerSentEvent<KAudioData>> withNodeAudio(String conversationId, String message,
                                                           Flux<NodeResultData> stream) {
        return withAudio(conversationId, message, stream.map(NodeResultData::toData));
    }

    public Flux<ServerSentEvent<KAudioData>> withAudio(String conversationId, String message,
                                                       Flux<DataWithMeta> stream) {
        Flux<KAudioData> audioStream = tts(conversationId, message, stream);
        return audioStream.map(new Function<KAudioData, ServerSentEvent<KAudioData>>() {
            @Override
            public ServerSentEvent<KAudioData> apply(KAudioData kAudioData) {
                return ServerSentEvent.<KAudioData>builder()
                        .id(String.valueOf(System.currentTimeMillis()))
                        .data(kAudioData)
                        .build();
            }
        });
    }

    public Flux<KAsrResult> asrBytes(String conversationId, String scene, Flux<byte[]> audioData) {
        return audioService.asr(conversationId, scene, audioData.map(ByteBuffer::wrap));
    }

    public Flux<KAsrResult> asr(String conversationId, String scene, Flux<ByteBuffer> audioData) {
        return audioService.asr(conversationId, scene, audioData);
    }

    public Flux<KAudioData> tts(String conversationId, String message,
                                Flux<DataWithMeta> stream) {
        long startTime = System.currentTimeMillis();
        log.info("withAudio方法开始时间: conversationId={}, startTime={}, message={}",
                conversationId, startTime, message);

        // 使用AtomicBoolean来确保线程安全地记录首次音频数据返回时间
        AtomicBoolean firstAudio = new AtomicBoolean(true);

        stream = stream.onErrorComplete();
        stream = stream.cache();
        Flux<String> textFlux = stream.map(data -> {
            if (data.data() != null) {
                return data.data();
            }
            return "";
        });
        Flux<KAudioData> meta = stream
                .filter(data -> !CollectionUtils.isEmpty(data.meta()))
                .map(data -> {
                    return KAudioData.ofMeta(data.meta());
                });
        Flux<KAudioData> adioStream = Flux.merge(audioService.tts(conversationId, textFlux), meta).cache();
        audioOutputSaveService.save(adioStream, conversationId, message);
        return adioStream.doOnNext(s -> {
            int length = 0;
            if (StringUtils.hasText(s.audio())) {
                length = s.audio().length();
            }
            // 记录首次音频数据返回的真正时间 - 只在真正有音频数据时记录
            if (firstAudio.get() && StringUtils.hasText(s.audio()) &&
                    ("audio".equals(s.type()) || "end".equals(s.type()))) {
                if (firstAudio.compareAndSet(true, false)) {
                    long firstAudioTime = System.currentTimeMillis();
                    long timeToFirstAudio = firstAudioTime - startTime;
                    log.info("首次音频数据返回时间: conversationId={}, firstAudioTime={}, 从withAudio开始到首次音频数据返回时间差={}ms, audioLength={}, type={}, text={}",
                            conversationId, firstAudioTime, timeToFirstAudio, length, s.type(), s.text());
                }
            }
            log.debug("返回用户语音:conversationId={},index={},text={},type={},audioLength={}",
                    conversationId, s.index(), s.text(), s.type(), length);
        });
    }
}
