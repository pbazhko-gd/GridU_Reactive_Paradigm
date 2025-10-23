package com.griddynamics.gridu.pbazhko.config;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

import static com.griddynamics.gridu.pbazhko.util.MdcHelper.REQUEST_ID_HEADER;
import static com.griddynamics.gridu.pbazhko.util.MdcHelper.REQUEST_ID_MDC_KEY;

@Component
public class LoggingWebFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
        throws ServletException, IOException {

        try {
            MDC.put(REQUEST_ID_MDC_KEY, getRequestId((HttpServletRequest) request));
            chain.doFilter(request, response);
        } finally {
            MDC.clear();
        }
    }

    private String getRequestId(HttpServletRequest request) {
        return Optional.ofNullable(request.getHeader(REQUEST_ID_HEADER))
            .orElse(UUID.randomUUID().toString());
    }
}
