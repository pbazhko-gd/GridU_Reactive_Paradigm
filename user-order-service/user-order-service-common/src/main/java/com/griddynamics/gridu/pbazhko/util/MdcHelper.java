package com.griddynamics.gridu.pbazhko.util;

import lombok.experimental.UtilityClass;
import org.slf4j.MDC;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.context.ContextView;

import java.util.function.Function;

@UtilityClass
public class MdcHelper {

    public static final String REQUEST_ID_HEADER = "X-Request-ID";
    public static final String REQUEST_ID_MDC_KEY = "requestId";

    public static <T> Function<Flux<T>, Flux<T>> useMdcForFlux() {
        return publisher -> Flux.from(publisher)
            .doOnEach(signal -> {
                if (signal.isOnNext() || signal.isOnComplete() || signal.isOnError()) {
                    ContextView ctx = signal.getContextView();
                    applyMdc(ctx);
                }
            })
            .doFinally(sig -> MDC.clear());
    }

    public static <T> Function<Mono<T>, Mono<T>> useMdcForMono() {
        return publisher -> Mono.from(publisher)
            .doOnEach(signal -> {
                if (signal.isOnNext() || signal.isOnComplete() || signal.isOnError()) {
                    ContextView ctx = signal.getContextView();
                    applyMdc(ctx);
                }
            })
            .doFinally(sig -> MDC.clear());
    }

    public static <T> Function<Throwable, Flux<T>> onErrorResumeWithMdcFlux(Function<Throwable, Flux<T>> handler) {
        return ex -> Flux.deferContextual(ctx -> {
            applyMdc(ctx);
            return handler.apply(ex).doFinally(sig -> MDC.clear());
        });
    }

    private static void applyMdc(ContextView ctx) {
        MDC.put(REQUEST_ID_MDC_KEY, ctx.getOrDefault(REQUEST_ID_MDC_KEY, "-"));
    }
}
