package me.study.userservice.service;

import lombok.RequiredArgsConstructor;
import me.study.userservice.client.OrderClientService;
import me.study.userservice.dto.UserDto;
import me.study.userservice.entity.UserEntity;
import me.study.userservice.exception.OverlapException;
import me.study.userservice.exception.UserNotFoundException;
import me.study.userservice.repository.UserRepository;
import me.study.userservice.vo.ResponseOrder;
import me.study.userservice.vo.ResponseUser;
import me.study.userservice.vo.ResponseUserPage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final OrderClientService orderClientService;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        UserEntity userEntity = userRepository.findByEmail(email);

        if(userEntity == null)
            throw new UsernameNotFoundException(email);

        return new User(userEntity.getEmail(), userEntity.getEncryptedPwd()
                , true, true, true, true
                , new ArrayList<>());
    }

    @Override
    public UserDto getUserDetailsByEmail(String email) {
        UserEntity userEntity = userRepository.findByEmail(email);

        if(userEntity == null)
            throw new UsernameNotFoundException(email);

        return new UserDto(userEntity);
    }

    @Override
    public ResponseUser createUser(UserDto userDto) {
        if(userRepository.existsByEmail(userDto.getEmail()))
            throw new OverlapException("Email", userDto.getEmail());

        userDto.setUserId(UUID.randomUUID().toString());
        userDto.setEncryptedPwd(bCryptPasswordEncoder.encode(userDto.getPwd()));

        UserEntity userEntity = userRepository.save(new UserEntity(userDto));

        return new ResponseUser(userEntity);
    }

    @Override
    public ResponseUserPage getUsers(Pageable pageable) {
        Page<UserEntity> userEntityPage = userRepository.findByAll(pageable);
        return new ResponseUserPage(userEntityPage);
    }

    @Override
    public ResponseUser getUser(String userId) {
        UserEntity userEntity = userRepository.findByUserId(userId);

        if(userEntity == null)
            throw new UserNotFoundException("Id", userId);

        UserDto userDto = new UserDto(userEntity);

        List<ResponseOrder> orderList = orderClientService.getOrders(userId);
        userDto.setOrders(orderList);

        return new ResponseUser(userDto);
    }
}
