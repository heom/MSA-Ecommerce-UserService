package me.study.userservice.controller;

import lombok.RequiredArgsConstructor;
import me.study.userservice.dto.UserDto;
import me.study.userservice.service.UserService;
import me.study.userservice.vo.RequestUser;
import me.study.userservice.vo.ResponseUser;
import me.study.userservice.vo.ResponseUserPage;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final Environment env;

    @GetMapping("/health-check")
    public String status(){
        return String.format("It's Working in User Service"
                + ", [localServer port] = %s"
                + ", [server port] = %s"
                + ", [token secret] = %s"
                + ", [token expiration_time] = %s"
                , env.getProperty("local.server.port")
                , env.getProperty("server.port")
                , env.getProperty("token.secret")
                , env.getProperty("token.expiration_time")
        );
    }

    @PostMapping("/users")
    public ResponseEntity<ResponseUser> createUser(@Valid @RequestBody RequestUser requestUser){
        ResponseUser responseUser = userService.createUser(new UserDto(requestUser));
        return ResponseEntity.status(HttpStatus.CREATED).body(responseUser);
    }

    @GetMapping("/users")
    public ResponseEntity<ResponseUserPage> getUsers(@PageableDefault(size = 10, sort = "createdDate"
                                                                        , direction = Sort.Direction.DESC) Pageable pageable){
        ResponseUserPage responseUserPage = userService.getUsers(pageable);
        return ResponseEntity.status(HttpStatus.OK).body(responseUserPage);
    }

    @GetMapping("/users/user")
    public ResponseEntity<ResponseUser> getUser(@RequestHeader("X-Authorization-Id") String userId){
        ResponseUser responseUser = userService.getUser(userId);
        return ResponseEntity.status(HttpStatus.OK).body(responseUser);
    }
}
