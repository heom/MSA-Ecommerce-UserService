package me.study.userservice.service;

import me.study.userservice.dto.UserDto;
import me.study.userservice.entity.UserEntity;
import me.study.userservice.exception.OverlapException;
import me.study.userservice.exception.UserNotFoundException;
import me.study.userservice.repository.UserRepository;
import me.study.userservice.vo.ResponseUser;
import me.study.userservice.vo.ResponseUserPage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class UserServiceImplTest {

    @Autowired
    private UserService userService;
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    @Autowired
    private UserRepository userRepository;
    @PersistenceContext
    private EntityManager em;

    @AfterEach
    public void afterEach(){
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("loadUserByUsername")
    public void loadUserByUsername(){
        //given
        String email = "test@email.com";
        String rawPwd = "testPassword";
        UserDto userDto = UserDto.builder()
                                    .email(email)
                                    .name("test")
                                    .pwd(rawPwd)
                                    .build();
        userService.createUser(userDto);

        //when
        UserDetails userDetails = userService.loadUserByUsername(email);

        //then
        assertAll(
                () -> assertEquals(userDetails.getUsername(), email),
                () -> assertTrue(bCryptPasswordEncoder.matches(rawPwd, userDetails.getPassword()))
        );
    }

    @Test
    @DisplayName("loadUserByUsername_not_found")
    public void loadUserByUsername_not_found(){
        //given
        String email = "test@email.com";

        //when & then
        assertThrows(UsernameNotFoundException.class, ()-> userService.loadUserByUsername(email));
    }

    @Test
    @DisplayName("getUserDetailsByEmail")
    public void getUserDetailsByEmail(){
        //given
        String email = "test@email.com";
        String name = "test";
        String rawPwd = "testPassword";
        UserDto userDto = UserDto.builder()
                                    .email(email)
                                    .name(name)
                                    .pwd(rawPwd)
                                    .build();
        userService.createUser(userDto);

        //when
        UserDto returnUserDto = userService.getUserDetailsByEmail(email);

        //then
        assertAll(
                () -> assertEquals(returnUserDto.getEmail(), email),
                () -> assertEquals(returnUserDto.getName(), name),
                () -> assertTrue(bCryptPasswordEncoder.matches(rawPwd, returnUserDto.getEncryptedPwd()))
        );
    }

    @Test
    @DisplayName("getUserDetailsByEmail_not_found")
    public void getUserDetailsByEmail_not_found(){
        //given
        String email = "test@email.com";

        //when & then
        assertThrows(UsernameNotFoundException.class, ()-> userService.getUserDetailsByEmail(email));
    }

    @Test
    @DisplayName("createUser")
    public void createUser(){
        //given
        String email = "test@email.com";
        String name = "test";
        UserDto userDto = UserDto.builder()
                                    .email(email)
                                    .name(name)
                                    .pwd("testPassword")
                                    .build();
        //when
        ResponseUser responseUser = userService.createUser(userDto);

        //then
        assertAll(
                () -> assertEquals(responseUser.getEmail(), email),
                () -> assertEquals(responseUser.getName(), name),
                () -> assertNotNull(responseUser.getUserId()),
                () -> assertNotNull(responseUser.getCreatedDate()),
                () -> assertNotNull(responseUser.getLastModifiedDate())
        );
    }

    @Test
    @DisplayName("createUser_overlap_email")
    public void createUser_overlap_email(){
        //given
        String email = "test@email.com";
        UserDto userDto = UserDto.builder()
                                    .email(email)
                                    .name("test")
                                    .pwd("testPassword")
                                    .build();
        userService.createUser(userDto);

        //when & then
        assertThrows(OverlapException.class, ()-> userService.createUser(userDto));
    }

    @Test
    @DisplayName("getUsers")
    public void getUsers(){
        //given
        List<UserEntity> userEntityList = new ArrayList<>();
        int totalCount = 4;
        for (int i = 0; i < totalCount; i++) {
            UserEntity userEntity = UserEntity.builder()
                                                .email("test@naver.com"+i)
                                                .name("test"+i)
                                                .userId("testId"+i)
                                                .encryptedPwd("1234")
                                                .build();
            userEntityList.add(userEntity);
        }
        userRepository.saveAll(userEntityList);

        em.flush();
        em.clear();

        //when
        int page = 0;
        int size = 3;
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "name"));
        ResponseUserPage responseUserPage = userService.getUsers(pageRequest);
        Long resultTotalCount = responseUserPage.getTotalCount();
        List<ResponseUser> list = responseUserPage.getList();

        //then
        assertThat(list).extracting("name")
                        .containsExactly("test3"
                                        ,"test2"
                                        ,"test1");

        assertAll(
                () -> assertEquals(resultTotalCount, totalCount),
                () -> assertEquals(list.size(), size)
        );
    }

    @Test
    @DisplayName("getUser")
    public void getUser(){
        //given
        UserDto userDto = UserDto.builder()
                                    .email("test@email.com")
                                    .name("test")
                                    .pwd("testPassword")
                                    .build();
        ResponseUser responseUser = userService.createUser(userDto);

        //when
        String userId = responseUser.getUserId();
        ResponseUser resultResponseUser = userService.getUser(userId);

        //then
        assertAll(
                () -> assertEquals(responseUser.getEmail(), resultResponseUser.getEmail()),
                () -> assertEquals(responseUser.getName(), resultResponseUser.getName()),
                () -> assertEquals(responseUser.getUserId(), resultResponseUser.getUserId()),
                () -> assertEquals(responseUser.getCreatedDate(), resultResponseUser.getCreatedDate()),
                () -> assertEquals(responseUser.getLastModifiedDate(), resultResponseUser.getLastModifiedDate()),
                () -> assertNull(responseUser.getOrders())
        );
    }

    @Test
    @DisplayName("getUser_not_found")
    public void getUser_not_found(){
        //given
        String email = "test@email.com";

        //when & then
        assertThrows(UserNotFoundException.class, ()-> userService.getUser(email));
    }

    @Test
    @DisplayName("getUserByAuth")
    public void getUserByAuth(){
        //given
        UserDto userDto = UserDto.builder()
                                    .email("test@email.com")
                                    .name("test")
                                    .pwd("testPassword")
                                    .build();
        ResponseUser responseUser = userService.createUser(userDto);

        //when
        String userId = responseUser.getUserId();
        ResponseUser resultResponseUser = userService.getUserByAuth(userId);

        //then
        assertAll(
                () -> assertEquals(responseUser.getEmail(), resultResponseUser.getEmail()),
                () -> assertEquals(responseUser.getName(), resultResponseUser.getName()),
                () -> assertEquals(responseUser.getUserId(), resultResponseUser.getUserId()),
                () -> assertEquals(responseUser.getCreatedDate(), resultResponseUser.getCreatedDate()),
                () -> assertEquals(responseUser.getLastModifiedDate(), resultResponseUser.getLastModifiedDate())
        );
    }

    @Test
    @DisplayName("getUserByAuth_not_found")
    public void getUserByAuth_not_found(){
        //given
        String email = "test@email.com";

        //when & then
        assertThrows(UserNotFoundException.class, ()-> userService.getUserByAuth(email));
    }

}