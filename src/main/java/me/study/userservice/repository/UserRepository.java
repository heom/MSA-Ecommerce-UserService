package me.study.userservice.repository;

import me.study.userservice.entity.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UserRepository extends JpaRepository<UserEntity, Long> {

    UserEntity findByUserId(String userId);

    boolean existsByEmail(String email);

    @Query(value="select u from UserEntity u"
            , countQuery = "select count(u.id) from UserEntity u")
    Page<UserEntity> findByAll(Pageable pageable);

    UserEntity findByEmail(String email);
}
