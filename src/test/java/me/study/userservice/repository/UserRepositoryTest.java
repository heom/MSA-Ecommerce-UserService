package me.study.userservice.repository;

import me.study.userservice.entity.UserEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
@Transactional
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;
    @PersistenceContext
    private EntityManager em;

    @Test
    @DisplayName("save")
    public void save() {
        //given
        UserEntity userEntity = UserEntity.builder()
                                            .email("test@naver.com")
                                            .name("test")
                                            .userId("testId")
                                            .encryptedPwd("1234")
                                            .build();

        //when
        UserEntity savedUserEntity = userRepository.save(userEntity);

        //then
        assertThat(userEntity).isEqualTo(savedUserEntity);
    }

    @Test
    @DisplayName("findByUserId")
    public void findByUserId() {
        //given
        String userId = "testId";
        UserEntity userEntity = UserEntity.builder()
                                            .email("test@naver.com")
                                            .name("test")
                                            .userId(userId)
                                            .encryptedPwd("1234")
                                            .build();
        userRepository.save(userEntity);

        //when
        UserEntity findUserEntity = userRepository.findByUserId(userId);

        //then
        assertThat(userEntity).isEqualTo(findUserEntity);
    }

    @Test
    @DisplayName("existsByEmail")
    public void existsByEmail() {
        //given
        String email = "test@naver.com";
        UserEntity userEntity = UserEntity.builder()
                                            .email(email)
                                            .name("test")
                                            .userId("testId")
                                            .encryptedPwd("1234")
                                            .build();
        userRepository.save(userEntity);

        //when
        boolean exists = userRepository.existsByEmail(email);

        //then
        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("findByAll")
    public void findByAll() {
        //given
        List<UserEntity> userEntityList = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
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
        Page<UserEntity> userEntityPage = userRepository.findByAll(pageRequest);

        //then
        assertThat(userEntityPage.getContent()).extracting("name")
                                                .containsExactly("test3"
                                                        ,"test2"
                                                        ,"test1");
        assertAll(
                () -> assertNotNull(userEntityPage.getContent()),
                () -> assertEquals(userEntityPage.getContent().size(), 3),
                () -> assertEquals(userEntityPage.getTotalElements(), 4),
                () -> assertEquals(userEntityPage.getSize(), size),
                () -> assertEquals(userEntityPage.getNumber(), page)
        );
    }

    @Test
    @DisplayName("findByEmail")
    public void findByEmail() {
        //given
        String email = "test@naver.com";
        UserEntity userEntity = UserEntity.builder()
                                            .email(email)
                                            .name("test")
                                            .userId("testId")
                                            .encryptedPwd("1234")
                                            .build();
        userRepository.save(userEntity);

        //when
        UserEntity findUserEntity = userRepository.findByEmail(email);

        //then
        assertThat(userEntity).isEqualTo(findUserEntity);
    }
}