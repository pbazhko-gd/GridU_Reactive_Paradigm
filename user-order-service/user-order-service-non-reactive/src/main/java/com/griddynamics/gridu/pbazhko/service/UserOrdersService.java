package com.griddynamics.gridu.pbazhko.service;

import com.griddynamics.gridu.pbazhko.dto.OrderDto;
import com.griddynamics.gridu.pbazhko.dto.UserInfoDto;
import com.griddynamics.gridu.pbazhko.dto.UserOrderDto;
import com.griddynamics.gridu.pbazhko.mapper.UserOrderMapper;
import com.griddynamics.gridu.pbazhko.util.MdcAwareExecutor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserOrdersService {

    private final UserInfoService userInfoService;
    private final OrderSearchService orderSearchService;
    private final ProductInfoService productInfoService;
    private final UserOrderMapper userOrderMapper;

    private final Executor executor = new MdcAwareExecutor(Executors.newFixedThreadPool(20));

    public List<UserOrderDto> findAllUserOrders() {
        log.info("Retrieving all users orders");
        var futures = userInfoService.findAllUsers().stream()
            .map(user -> CompletableFuture.supplyAsync(() -> findOrdersForUser(user), executor))
            .map(future -> future.handle((res, ex) -> {
                if (ex != null) {
                    log.error("Error in completable future", ex);
                    return null;
                }
                return res;
            }))
            .toList();

        var allOf = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));

        return allOf.thenApply(v -> futures.stream()
            .map(CompletableFuture::join)
            .flatMap(List::stream)
            .toList()
        ).join();
    }

    public List<UserOrderDto> findOrdersByUserId(String userId) {
        log.info("Retrieving user orders by userId='{}'", userId);
        var user = userInfoService.findUserById(userId);
        return findOrdersForUser(user);
    }

    private List<UserOrderDto> findOrdersForUser(UserInfoDto userInfo) {
        var orders = orderSearchService.findOrdersByPhone(userInfo.getPhone());
        var futures = orders.stream()
            .map(order -> CompletableFuture.supplyAsync(() -> getUserOrder(userInfo, order), executor))
            .map(future -> future.handle((res, ex) -> {
                if (ex != null) {
                    log.error("Error in completable future", ex);
                    return null;
                }
                return res;
            }))
            .toList();

        var allOf = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));

        return allOf.thenApply(v -> futures.stream()
            .map(CompletableFuture::join)
            .toList()
        ).join();
    }

    private UserOrderDto getUserOrder(UserInfoDto userInfo, OrderDto order) {
        var product = productInfoService.findTheMostRelevantProductByCode(order.getProductCode());
        return userOrderMapper.toDto(userInfo, order, product);
    }
}
