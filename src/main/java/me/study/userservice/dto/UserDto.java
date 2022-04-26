package me.study.userservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import me.study.userservice.entity.UserEntity;
import me.study.userservice.vo.RequestUser;
import me.study.userservice.vo.ResponseOrder;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder @AllArgsConstructor
public class UserDto {

    private String email;
    private String name;
    private String pwd;
    private String userId;
    private LocalDateTime createdDate;
    private LocalDateTime lastModifiedDate;
    private String encryptedPwd;
    private List<ResponseOrder> orders;

    public UserDto(RequestUser requestUser){
        this.email = requestUser.getEmail();
        this.name = requestUser.getName();
        this.pwd = requestUser.getPwd();
    }

    public UserDto(UserEntity userEntity){
        this.email = userEntity.getEmail();
        this.name = userEntity.getName();
        this.userId = userEntity.getUserId();
        this.encryptedPwd = userEntity.getEncryptedPwd();
        this.createdDate = userEntity.getCreatedDate();
        this.lastModifiedDate = userEntity.getLastModifiedDate();
    }
}
