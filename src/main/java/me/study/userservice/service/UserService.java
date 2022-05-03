package me.study.userservice.service;

import me.study.userservice.dto.UserDto;
import me.study.userservice.vo.ResponseUser;
import me.study.userservice.vo.ResponseUserPage;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetailsService;


public interface UserService extends UserDetailsService {

    ResponseUser createUser(UserDto userDto);
    UserDto getUserDetailsByEmail(String email);
    ResponseUserPage getUsers(Pageable pageable);
    ResponseUser getUser(String userId);
}
