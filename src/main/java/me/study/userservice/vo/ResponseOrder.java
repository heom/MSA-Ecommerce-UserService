package me.study.userservice.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ResponseOrder {
    private String productId;
    private Integer qty;
    private Integer unitPrice;
    private Integer totalPrice;
    private LocalDateTime createdDate;
    private LocalDateTime lastModifiedDate;

    private String orderId;
}
