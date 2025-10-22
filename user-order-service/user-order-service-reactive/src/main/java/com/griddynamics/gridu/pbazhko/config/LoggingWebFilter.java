package com.griddynamics.gridu.pbazhko.config;

import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;
import reactor.util.context.Context;

import java.util.Optional;
import java.util.UUID;

import static com.griddynamics.gridu.pbazhko.util.MdcHelper.REQUEST_ID_MDC_KEY;

@Component
public class LoggingWebFilter implements WebFilter {

    private static final String REQUEST_ID_HEADER = "X-Request-ID";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        return chain.filter(exchange)
            .contextWrite(Context.of(REQUEST_ID_MDC_KEY, getRequestId(exchange)));
    }

    private String getRequestId(ServerWebExchange exchange) {
        return Optional.ofNullable(
            exchange.getRequest()
                .getHeaders()
                .getFirst(REQUEST_ID_HEADER)
        ).orElse(UUID.randomUUID().toString());
    }
}
