package me.study.userservice.client;

import lombok.extern.slf4j.Slf4j;
import me.study.userservice.vo.ResponseOrder;
import org.springframework.cloud.client.circuitbreaker.CircuitBreaker;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class OrderClientService {

    private final OrderClient orderClient;
    private final CircuitBreaker circuitBreaker;

    public OrderClientService(OrderClient orderClient, CircuitBreakerFactory circuitBreakerFactory) {
        this.orderClient = orderClient;
        this.circuitBreaker = circuitBreakerFactory.create("circuitbreaker");
    }

    public List<ResponseOrder> getOrders(String userId){
        return circuitBreaker.run(() -> orderClient.getOrders(userId), throwable -> new ArrayList<>());
    }
}
