package com.griddynamics.reactive.course.ordersearchservice.service;


import com.griddynamics.reactive.course.ordersearchservice.domain.Order;
import com.griddynamics.reactive.course.ordersearchservice.resource.OrderResource;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import wiremock.org.apache.commons.lang3.StringUtils;

import java.time.Duration;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class OrderSearchService {

    private final OrderResource orderResource;
    private final Random random = new Random();

    public Flux<Order> getOrdersByPhone(String phoneNumber) {

        if (StringUtils.isBlank(phoneNumber)) return Flux.error(new RuntimeException("Phone number is empty"));

        return Flux.fromIterable(orderResource.getOrdersByPhone(phoneNumber))
                .delayElements(Duration.ofMillis(randomInt()));
    }

    private int randomInt() {
        int min = 100;
        int max = 1000;
        return random.nextInt(max - min) + min;
    }
}
