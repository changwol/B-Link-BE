package com.blink.server.user.controller;

import com.blink.server.user.dto.UserSingUpDto;
import com.blink.server.user.entity.User;
import com.blink.server.user.repository.UserRepository;
import com.blink.server.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
@CrossOrigin(origins = "http://localhost:3000")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;

    /*
    회원가입 메서드
     */
    @PostMapping("/join")
    public Mono<ResponseEntity<UserSingUpDto>> joinUser(@RequestBody UserSingUpDto dto) {
        return handleMessage(dto)
                .map(savedMessage -> ResponseEntity.status(HttpStatus.CREATED).body(savedMessage))
                .defaultIfEmpty(ResponseEntity.status(HttpStatus.BAD_REQUEST).build());
    }

    private Mono<UserSingUpDto> handleMessage(UserSingUpDto messageDto) {
        return userService.saveUser(messageDto)
                .map(UserSingUpDto::of)
                .cast(UserSingUpDto.class);
    }

    @GetMapping("idCheck")
    /*
     * ID 중복 체크 메서드 (단독 사용)
     */
    public ResponseEntity<?> idCheck(@RequestParam String userId) {
        if (userService.canUseThisUserId(userId)) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.badRequest().build();
    }

    @GetMapping("/search")
    @ResponseBody
    public Mono<ResponseEntity<?>> searchUsers(@RequestParam String userName) {
        logger.info("Searching for user: {}", userName);

        return userService.findByUserName(userName)
                .collectList() // 결과를 리스트로 수집
                .flatMap(users -> {
                    if (users.isEmpty()) {
                        return Mono.just(ResponseEntity.notFound().build());
                    } else {
                        // 여러 사용자 정보를 포함하는 리스트 생성
                        List<Map<String, Object>> responseList = new ArrayList<>();
                        for (User user : users) {
                            Map<String, Object> responseMap = new HashMap<>();
                            responseMap.put("userName", user.getUserName());
                            responseMap.put("userId", user.getUserId());
                            responseList.add(responseMap);
                        }
                        return Mono.just(ResponseEntity.ok(responseList));
                    }
                });
    }


    @GetMapping("alluser")
    public Flux<ResponseEntity<?>> allUser() {
        return userRepository.findAll()
                .map(UserSingUpDto::of)
                .collectList() // 리스트 수집
                .flatMapMany(users -> {
                    if (users.isEmpty()) {
                        return Flux.just(ResponseEntity.notFound().build());
                    } else {
                        return Flux.just(ResponseEntity.ok(users));
                    }
                });
    }
}
