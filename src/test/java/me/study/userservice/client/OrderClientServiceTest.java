package me.study.userservice.client;

import me.study.userservice.vo.ResponseOrder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class OrderClientServiceTest {

    @Autowired
    private OrderClientService orderClientService;

    @Test
    @DisplayName("getOrders")
    public void getOrders(){
        //given
        String userId = "test";

        //when
        List<ResponseOrder> getOrders = orderClientService.getOrders(userId);

        //then
        assertThat(getOrders.size()).isEqualTo(0);
    }
}