package me.study.userservice.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.study.userservice.dto.UserDto;
import me.study.userservice.entity.UserEntity;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseUser {
    private String email;
    private String name;
    private String userId;
    private LocalDateTime createdDate;
    private LocalDateTime lastModifiedDate;
    private List<ResponseOrder> orders;

    public ResponseUser(UserDto userDto){
        this.email = userDto.getEmail();
        this.name = userDto.getName();
        this.userId = userDto.getUserId();
        this.orders = userDto.getOrders();
        this.createdDate = userDto.getCreatedDate();
        this.lastModifiedDate = userDto.getLastModifiedDate();
    }

    public ResponseUser(UserEntity userEntity){
        this.email = userEntity.getEmail();
        this.name = userEntity.getName();
        this.userId = userEntity.getUserId();
        this.createdDate = userEntity.getCreatedDate();
        this.lastModifiedDate = userEntity.getLastModifiedDate();
    }
}
