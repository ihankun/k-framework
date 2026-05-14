package io.hankun.framework.ai.core.util;

import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * @description:
 * @className: FluxUtil
 * @createAt: 2025/10/9 10:12
 * @author: hankun
 */
@Slf4j
public class FluxUtil {

    public static Flux<String> redistrict(Flux<String> resultStream) {
        return toCharacterFlux(resultStream)
                .windowUntil(FluxUtil::checkSplit)
                .concatMap(window -> window.collect(StringBuilder::new, StringBuilder::append))
                .map(StringBuilder::toString);
    }

    public static Flux<String> redistrict(Flux<String> resultStream, int maxSize, Duration duration) {
        return windowUntilOrTimeout(toCharacterFlux(resultStream), maxSize, duration, FluxUtil::checkSplit)
                .map(list -> {
                    StringBuilder stringBuilder = new StringBuilder();
                    for (Character s : list) {
                        stringBuilder.append(s);
                    }
                    return stringBuilder.toString();
                });
    }

    public static Flux<String> redistrictChars(Flux<Character> resultStream) {
        return resultStream
                .windowUntil(FluxUtil::checkSplit)
                .concatMap(window -> window.collect(StringBuilder::new, StringBuilder::append))
                .map(StringBuilder::toString);
    }

    public static char[] splitChars = {'。', '！', '？', '；', '!', '?', ';', '，', '，', ','};

    public static boolean checkSplit(char c) {
        for (char splitChar : splitChars) {
            if (c == splitChar) {
                return true;
            }
        }
        return false;
    }

    public static <T> String collectToString(Flux<T> stream) {
        try {
            return stream.collect(StringBuilder::new, StringBuilder::append).toFuture().get().toString();
        } catch (InterruptedException e) {
            log.error("flux interrupted");
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            log.error("flux execution failed");
            throw new RuntimeException(e);
        }
    }

    public static Flux<Character> toCharacterFlux(Flux<String> resultStream) {
        return resultStream.
                concatMap(s -> Flux.fromIterable(s.chars().mapToObj(c -> (char) c).toList()));
    }


    public static <T, R> Flux<R> timeWait(Flux<T> paramsFlux, int maxSize, Duration duration
            , Function<List<T>, List<R>> merge) {
        return paramsFlux.windowTimeout(maxSize, duration)
                .concatMap(Flux::collectList)
                .concatMap(new Function<List<T>, Publisher<R>>() {
                    @Override
                    public Publisher<R> apply(List<T> agentCallParams) {
                        return Flux.fromIterable(merge.apply(agentCallParams));
                    }
                });
    }


    public static <T> Flux<List<T>> windowUntilOrTimeout(Flux<T> source, int maxSize,
                                                         Duration timeout, Predicate<T> condition) {
        return source.windowUntil(condition)
                .concatMap(window -> {
                    return window.bufferTimeout(maxSize, timeout);
                });
    }
}
