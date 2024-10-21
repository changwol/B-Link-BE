package com.blink.server.user.controller;

import com.blink.server.user.dto.UserSingUpDto;
import com.blink.server.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {
    private final UserService userService;

    /*
    회원가입 메서드
     */
    @PostMapping("/join")
    public ResponseEntity<?> joinUser(@RequestBody UserSingUpDto dto) {
        if(!userService.canUseThisUserId(dto.getUserId())){ // ID 중복 시 가입 거절 (이중 체크)
            return ResponseEntity.badRequest().build();
        }
        userService.joinUser(dto);
        return ResponseEntity.ok().build();
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

}
