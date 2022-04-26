package me.study.userservice.entity;

import lombok.*;
import me.study.userservice.dto.UserDto;

import javax.persistence.*;

@Entity @Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder @AllArgsConstructor
@Table(name = "users")
public class UserEntity extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50, unique = true)
    private String email;
    @Column(nullable = false, length = 50)
    private String name;
    @Column(nullable = false, unique = true)
    private String userId;
    @Column(nullable = false)
    private String encryptedPwd;

    public UserEntity(UserDto userDto){
        this.email = userDto.getEmail();
        this.name = userDto.getName();
        this.userId = userDto.getUserId();
        this.encryptedPwd = userDto.getEncryptedPwd();
    }
}
