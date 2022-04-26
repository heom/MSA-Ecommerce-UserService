package me.study.userservice.common;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class EncodeTest {

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Test
    @DisplayName("Password Encode")
    public void test(){
        //given
        String rawPwd = "password";

        //when
        String encodedPwd = bCryptPasswordEncoder.encode(rawPwd);

        //then
        assertAll(
                () -> assertNotEquals(rawPwd, encodedPwd),
                () -> assertTrue(bCryptPasswordEncoder.matches(rawPwd, encodedPwd))
        );
    }
}
