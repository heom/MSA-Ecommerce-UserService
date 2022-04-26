package me.study.userservice.controller;

import me.study.userservice.dto.UserDto;
import me.study.userservice.repository.UserRepository;
import me.study.userservice.service.UserService;
import me.study.userservice.vo.RequestLogin;
import me.study.userservice.vo.RequestUser;
import me.study.userservice.vo.ResponseUser;
import me.study.userservice.vo.ResponseUserPage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.core.env.Environment;
import org.springframework.http.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UserControllerTest {

    @LocalServerPort
    private int port;
    @Autowired
    private Environment env;
    @Autowired
    private TestRestTemplate restTemplate;
    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;

    @AfterEach
    public void afterEach(){
        userRepository.deleteAll();
    }

    private String getApiUrl(String url){
        StringBuffer sbf = new StringBuffer();
        return sbf.append("http://")
                .append(env.getProperty("gateway.ip"))
                .append(':')
                .append(this.port)
                .append(url).toString();
    }

    @Test
    @DisplayName("createUser")
    public void createUser(){
        //given
        String url = getApiUrl("/users");
        String email = "test@email.com";
        String name = "test";
        RequestUser requestUser = RequestUser.builder()
                                                .email(email)
                                                .name(name)
                                                .pwd("testPassword")
                                                .build();

        //when
        ResponseEntity<ResponseUser> responseEntity = this.restTemplate.postForEntity(url, requestUser, ResponseUser.class);

        //then
        assertAll(
                () -> assertEquals(responseEntity.getStatusCode(), HttpStatus.CREATED),
                () -> assertEquals(responseEntity.getBody().getEmail(), email),
                () -> assertEquals(responseEntity.getBody().getName(), name),
                () -> assertNotNull(responseEntity.getBody().getUserId()),
                () -> assertNotNull(responseEntity.getBody().getCreatedDate()),
                () -> assertNotNull(responseEntity.getBody().getLastModifiedDate())
        );
    }

    @Test
    @DisplayName("createUser_parameter_error")
    public void createUser_parameter_error(){
        //given
        String url = getApiUrl("/users");
        RequestUser requestUser = RequestUser.builder()
                                            .email("")
                                            .name("")
                                            .pwd("testPassword")
                                            .build();

        //when
        ResponseEntity<ResponseUser> responseEntity = this.restTemplate.postForEntity(url, requestUser, ResponseUser.class);

        //then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("createUser_overlap_email")
    public void createUser_overlap_email(){
        //given
        String url = getApiUrl("/users");
        RequestUser requestUser = RequestUser.builder()
                                                .email("test@email.com")
                                                .name("test")
                                                .pwd("testPassword")
                                                .build();
        userService.createUser(new UserDto(requestUser));

        //when
        ResponseEntity<ResponseUser> responseEntity = this.restTemplate.postForEntity(url, requestUser, ResponseUser.class);

        //then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("login")
    public void login(){
        //given
        String url = getApiUrl("/login");
        String email = "test@email.com";
        String name = "test";
        String pwd = "testPassword";
        RequestUser requestUser = RequestUser.builder()
                                                .email(email)
                                                .name(name)
                                                .pwd(pwd)
                                                .build();
        userService.createUser(new UserDto(requestUser));

        RequestLogin requestLogin = RequestLogin.builder()
                                                .email(email)
                                                .pwd(pwd)
                                                .build();

        //when
        ResponseEntity<ResponseUser> responseEntity = this.restTemplate.postForEntity(url, requestLogin, ResponseUser.class);

        //then
        assertAll(
                () -> assertEquals(responseEntity.getStatusCode(), HttpStatus.OK),
                () -> assertEquals(responseEntity.getBody().getEmail(), email),
                () -> assertEquals(responseEntity.getBody().getName(), name),
                () -> assertNotNull(responseEntity.getBody().getUserId()),
                () -> assertNotNull(responseEntity.getBody().getCreatedDate()),
                () -> assertNotNull(responseEntity.getBody().getLastModifiedDate())
        );
    }

    @Test
    @DisplayName("login_password_error")
    public void login_password_error(){
        //given
        String url = getApiUrl("/login");
        String email = "test@email.com";
        String pwd = "testPassword";
        RequestUser requestUser = RequestUser.builder()
                                                .email(email)
                                                .name("test")
                                                .pwd(pwd)
                                                .build();
        userService.createUser(new UserDto(requestUser));

        RequestLogin requestLogin = RequestLogin.builder()
                                                .email(email)
                                                .pwd(pwd+1)
                                                .build();

        //when
        ResponseEntity<ResponseUser> responseEntity = this.restTemplate.postForEntity(url, requestLogin, ResponseUser.class);

        //then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    @DisplayName("login_not_found")
    public void login_not_found(){
        //given
        String url = getApiUrl("/login");
        String email = "test@email.com";
        String pwd = "testPassword";

        RequestLogin requestLogin = RequestLogin.builder()
                                                .email(email)
                                                .pwd(pwd+1)
                                                .build();

        //when
        ResponseEntity<ResponseUser> responseEntity = this.restTemplate.postForEntity(url, requestLogin, ResponseUser.class);

        //then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    @DisplayName("getUsers")
    public void getUsers(){
        //given
        int page = 0;
        int size = 3;
        String url = getApiUrl("/users?page="+page+"&size="+size);

        //when
        ResponseEntity<ResponseUserPage> responseEntity = this.restTemplate.getForEntity(url, ResponseUserPage.class);

        //then
        assertAll(
                () -> assertEquals(responseEntity.getStatusCode(), HttpStatus.OK),
                () -> assertEquals(responseEntity.getBody().getTotalCount(), 0),
                () -> assertEquals(responseEntity.getBody().getList().size(), 0)
        );
    }

    @Test
    @DisplayName("getUser")
    public void getUser(){
        //given
        String url = getApiUrl("/users/user");
        String email = "test@email.com";
        String name = "test";
        RequestUser requestUser = RequestUser.builder()
                .email(email)
                .name(name)
                .pwd("testPassword")
                .build();
        ResponseUser user = userService.createUser(new UserDto(requestUser));
        String userId = user.getUserId();
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Authorization-Id", userId);

        //when
        ResponseEntity<ResponseUser> responseEntity = this.restTemplate.exchange(url, HttpMethod.GET,  new HttpEntity<>(headers), ResponseUser.class);

        //then
        assertAll(
                () -> assertEquals(responseEntity.getStatusCode(), HttpStatus.OK),
                () -> assertEquals(responseEntity.getBody().getName(), name),
                () -> assertEquals(responseEntity.getBody().getEmail(), email),
                () -> assertEquals(responseEntity.getBody().getUserId(), userId),
                () -> assertNotNull(responseEntity.getBody().getCreatedDate()),
                () -> assertNotNull(responseEntity.getBody().getLastModifiedDate())
        );
    }

    @Test
    @DisplayName("getUser_not_found")
    public void getUser_not_found(){
        //given
        String url = getApiUrl("/users/user");
        String userId = "test";
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Authorization-Id", userId);

        //when
        ResponseEntity<ResponseUser> responseEntity = this.restTemplate.exchange(url, HttpMethod.GET,  new HttpEntity<>(headers), ResponseUser.class);

        //then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("getUserByAuth")
    public void getUserByAuth(){
        //given
        String email = "test@email.com";
        String name = "test";
        RequestUser requestUser = RequestUser.builder()
                .email(email)
                .name(name)
                .pwd("testPassword")
                .build();
        ResponseUser user = userService.createUser(new UserDto(requestUser));
        String userId = user.getUserId();
        String url = getApiUrl("/users/"+userId);

        //when
        ResponseEntity<ResponseUser> responseEntity = this.restTemplate.getForEntity(url, ResponseUser.class);

        //then
        assertAll(
                () -> assertEquals(responseEntity.getStatusCode(), HttpStatus.OK),
                () -> assertEquals(responseEntity.getBody().getName(), name),
                () -> assertEquals(responseEntity.getBody().getEmail(), email),
                () -> assertEquals(responseEntity.getBody().getUserId(), userId),
                () -> assertNotNull(responseEntity.getBody().getCreatedDate()),
                () -> assertNotNull(responseEntity.getBody().getLastModifiedDate())
        );
    }

    @Test
    @DisplayName("getUserByAuth_not_found")
    public void getUserByAuth_not_found(){
        //given
        String userId = "test";
        String url = getApiUrl("/users/"+userId);

        //when
        ResponseEntity<ResponseUser> responseEntity = this.restTemplate.getForEntity(url, ResponseUser.class);

        //then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }
}
