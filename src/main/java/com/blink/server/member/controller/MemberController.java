package com.blink.server.member.controller;

import com.blink.server.jwt.JwToken;
import com.blink.server.member.dto.MemberInfoDto;
import com.blink.server.member.dto.MemberLoginDto;
import com.blink.server.member.dto.MemberSingUpDto;
import com.blink.server.member.repository.MemberRepository;
import com.blink.server.member.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping("/member")
@Tag(name = "Member", description = "사용자 관련 API 입니다.")
public class MemberController {

    private final MemberService memberService;
    private final MemberRepository memberRepository;

    /*
    회원가입 메서드
     */
    @PostMapping("/join")
    @Operation(summary = "회원 가입하기", description = "회원가입 메서드입니다. DTO 에 있는 정보들을 객체로 넘겨주면 됩니다.")
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
     */
    @GetMapping("/memberlist")
    @Operation(summary = "모든 유저 찾기", description = "모든 유저를 List 로 리턴하는 기능입니다.")
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
     *
     * @param dto Id / passWord
     */
    @PostMapping("/login")
    @Operation(summary = "로그인", description = "userId 와 userPassWord 로 로그인합니다. 성공시 JWT 를 리턴합니다.")
    public Mono<ResponseEntity<String>> login(@RequestBody MemberLoginDto dto) {
        System.out.println("dto.getUserId() = " + dto.getUserId());
        System.out.println("dto.getUserPassWord() = " + dto.getUserPassWord());
        JwToken token = memberService.loginMember(dto.getUserId(), dto.getUserPassWord());
        return Mono.just(ResponseEntity.ok().body(token.getAccessToken()));
    }

    /**
     * MyPage 에 필요한 정보들을 return 해주는 메서드.
     * @return MemberInfoDto
     */
    @GetMapping("/mypage")
    @Operation(summary = "마이페이지",description = "마이페이지에 필요한 정보들을 제공해주는 메서드입니다. JWT 가 필요합니다.")
    public Mono<ResponseEntity<MemberInfoDto>> getMemberInfomation() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = authentication.getName();
        System.out.println("userId = " + userId);

        return memberService.getMemberInfomation(userId)
                .map(memberInfo -> ResponseEntity.ok(memberInfo))
                .switchIfEmpty(Mono.just(ResponseEntity.notFound().build()));
    }

}
