package me.study.userservice.vo;

import lombok.Data;
import lombok.NoArgsConstructor;
import me.study.userservice.entity.UserEntity;
import org.springframework.data.domain.Page;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class ResponseUserPage {
    private Long totalCount;
    private List<ResponseUser> list;

    public ResponseUserPage(Page<UserEntity> userEntityPage){
        this.totalCount = userEntityPage.getTotalElements();
        List<ResponseUser> responseUserList = new ArrayList<>();
        userEntityPage.getContent().forEach(userEntity -> {
            responseUserList.add(new ResponseUser(userEntity));
        });
        this.list = responseUserList;
    }
}
