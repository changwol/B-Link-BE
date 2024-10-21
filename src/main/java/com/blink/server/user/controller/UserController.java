package com.blink.server.user.controller;

import com.blink.server.user.dto.UserSingUpDto;
import com.blink.server.user.repository.UserRepository;
import com.blink.server.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {

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


    @GetMapping("alluser")
    public Flux<ResponseEntity<?>> allUser() {
        return userRepository.findAll()
                .map(UserSingUpDto::of)
                .collectList()//리스트 수집
                .flatMapMany(users -> {
                    if (users.isEmpty()) {
                        return Flux.just(ResponseEntity.notFound().build());
                    } else {
                        return Flux.just(ResponseEntity.ok(users));
                    }
                });
    }
}
