package com.griddynamics.gridu.pbazhko.util;

import lombok.RequiredArgsConstructor;
import org.slf4j.MDC;

import java.util.Map;
import java.util.concurrent.Executor;

@RequiredArgsConstructor
public class MdcAwareExecutor implements Executor {

    private final Executor delegate;

    @Override
    public void execute(Runnable command) {
        Map<String, String> contextMap = MDC.getCopyOfContextMap();
        delegate.execute(() -> {
            if (contextMap != null) MDC.setContextMap(contextMap);
            else MDC.clear();
            try {
                command.run();
            } finally {
                MDC.clear();
            }
        });
    }
}
