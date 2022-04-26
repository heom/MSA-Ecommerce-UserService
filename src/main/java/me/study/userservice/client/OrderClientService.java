package me.study.userservice.client;

import lombok.RequiredArgsConstructor;
import me.study.userservice.vo.ResponseOrder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderClientService {

    private final OrderClient orderClient;

    public List<ResponseOrder> getOrders(String userId){
        return orderClient.getOrders(userId);
    }
}
