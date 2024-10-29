package com.blink.server.member.controller;

import com.blink.server.jwt.JwToken;
import com.blink.server.member.dto.MemberLoginDto;
import com.blink.server.member.dto.MemberSingUpDto;
import com.blink.server.member.repository.MemberRepository;
import com.blink.server.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping("/member")
public class MemberController {

    private final MemberService memberService;
    private final MemberRepository memberRepository;

    /*
    회원가입 메서드
     */
    @PostMapping("/join")
    public Mono<ResponseEntity<MemberSingUpDto>> joinUser(@RequestBody MemberSingUpDto dto) {
        return handleMessage(dto)
                .map(savedMessage -> ResponseEntity.status(HttpStatus.CREATED).body(savedMessage))
                .defaultIfEmpty(ResponseEntity.status(HttpStatus.BAD_REQUEST).build());
    }

    private Mono<MemberSingUpDto> handleMessage(MemberSingUpDto messageDto) {
        return memberService.saveUser(messageDto)
                .map(MemberSingUpDto::of)
                .cast(MemberSingUpDto.class);
    }

    /**
     * 모든 유저 불러오는 메서드
     * @return
     */
    @GetMapping("/memberlist")
    public Flux<ResponseEntity<?>> allUser() {
        return memberRepository.findAll()
                .map(MemberSingUpDto::of)
                .collectList()//리스트 수집
                .flatMapMany(users -> {
                    if (users.isEmpty()) {
                        return Flux.just(ResponseEntity.notFound().build());
                    } else {
                        return Flux.just(ResponseEntity.ok(users));
                    }
                });
    }
    /**
     * 로그인하는 메서드
     * @param dto Id / passWord
     * @return
     */
    @PostMapping("/login")
    public Mono<ResponseEntity<String>> login(@RequestBody MemberLoginDto dto) {
        System.out.println("dto.getUserId() = " + dto.getUserId());
        System.out.println("dto.getUserPassWord() = " + dto.getUserPassWord());
        JwToken token = memberService.loginMember(dto.getUserId(), dto.getUserPassWord());
        return Mono.just(ResponseEntity.ok().body(token.getAccessToken()));
    }
}
