package com.blink.server.user.service;

import com.blink.server.chat.entity.Message;
import com.blink.server.user.dto.UserSingUpDto;
import com.blink.server.user.entity.User;
import com.blink.server.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    public boolean canUseThisUserId(String userId) {
        return true; // DB 조회해서 구현 필요
    }

//    public void joinUser(UserSingUpDto dto) {
//        User user = new User();
//        user = user.UserDtoToEntity(dto);
//        userRepository.save(user);
//    }
    @Transactional
    public Mono<User> saveUser(UserSingUpDto dto) {
        User user = new User();
        user.setUserId(dto.getUserId());
        user.setUserPassWord(dto.getUserPassWord());
        user.setUserName(dto.getUserName());
        user.setUserEmail(dto.getUserEmail());
        user.setUserTel(dto.getUserTel());
        user.setUserStudentNumber(String.valueOf(dto.getUserStudentNumber()));
        user.setUserRegDate(dto.getUserRegDate());
        user.setUserBirthDate(dto.getUserBirthDate());
        user.setUserSex(dto.isUserSex());
        logger.info("Saving user: ID={}, Name={}, Email={}, Tel={}, Student Number={}, Registration Date={}, Birth Date={}",
                user.getUserId(),
                user.getUserName(),
                user.getUserEmail(),
                user.getUserTel(),
                user.getUserStudentNumber(),
                user.getUserRegDate(),
                user.getUserBirthDate());

        return userRepository.save(user);
    }
}
