package com.griddynamics.gridu.pbazhko.util;

import lombok.experimental.UtilityClass;
import org.reactivestreams.Publisher;
import org.slf4j.MDC;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.function.Function;

@UtilityClass
public class MdcHelper {

    public static final String REQUEST_ID_MDC_KEY = "requestId";

    public static <T> Function<Publisher<T>, Publisher<T>> applyContextForMdc() {
        return publisher -> {
            if (publisher instanceof Mono<T> mono) {
                return mono.flatMap(value -> Mono.deferContextual(ctx -> {
                    MDC.put(REQUEST_ID_MDC_KEY, ctx.get(REQUEST_ID_MDC_KEY));
                    return Mono.just(value).doFinally(sig -> MDC.clear());
                }));
            } else if (publisher instanceof Flux<T> flux) {
                return flux.flatMap(value -> Mono.deferContextual(ctx -> {
                    MDC.put(REQUEST_ID_MDC_KEY, ctx.get(REQUEST_ID_MDC_KEY));
                    return Mono.just(value).doFinally(sig -> MDC.clear());
                }));
            } else {
                throw new IllegalArgumentException("Unsupported publisher type");
            }
        };
    }
}
