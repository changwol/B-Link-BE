package com.blink.server.member.service;

import com.blink.server.jwt.JwToken;
import com.blink.server.jwt.JwTokenProvider;
import com.blink.server.member.dto.MemberSingUpDto;
import com.blink.server.member.entity.Member;
import com.blink.server.member.repository.MemberRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class MemberService {
    private static final Logger logger = LoggerFactory.getLogger(MemberService.class);

    private final MemberRepository memberRepository;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final JwTokenProvider jwTokenProvider;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public boolean canUseThisUserId(String userId) {
        return true; // DB 조회해서 구현 필요
    }

    /**
     * 회원가입
     * @param dto 회원가입 위한 DTO
     * @return
     */
    @Transactional
    public Mono<Member> saveUser(MemberSingUpDto dto) {
        Member member = new Member();
        member.setMemberId(dto.getUserId());
        member.setMemberPassWord(bCryptPasswordEncoder.encode(dto.getUserPassWord()));
        member.setMemberName(dto.getUserName());
        member.setMemberEmail(dto.getUserEmail());
        member.setMemberTel(dto.getUserTel());
        member.setMemberStudentNumber(String.valueOf(dto.getUserStudentNumber()));
        member.setMemberRegDate(dto.getUserRegDate());
        member.setMemberBirthDate(dto.getUserBirthDate());
        member.setMemberSex(dto.isUserSex());
        List<String> roles = new ArrayList<>();
        roles.add("member_student");
        member.setMemberRoles(roles);
        logger.info("Saving user: ID={}, Name={}, Email={}, Tel={}, Student Number={}, Registration Date={}, Birth Date={}",
                member.getMemberId(),
                member.getMemberName(),
                member.getMemberEmail(),
                member.getMemberTel(),
                member.getMemberStudentNumber(),
                member.getMemberRegDate(),
                member.getMemberBirthDate());

        return memberRepository.save(member);
    }

    /**
     * 로그인 하는 메서드, 로그인 성공시 JWT 토큰 반환
     * @param userName user ID
     * @param password user PassWord
     * @return JWT 토큰 반환
     */
    public JwToken loginMember(String userName, String password) {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userName, password);

        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

        JwToken jwToken = jwTokenProvider.generateToken(authentication);
        System.out.println(jwToken);
        return jwToken;
    }

    public Mono<Boolean> isThisPasswordMatch(String memberId ,String memberPassword) {
        Mono<Member> findMember = memberRepository.findByMemberId(memberId);
        return findMember.map(member -> bCryptPasswordEncoder.matches(memberPassword, member.getMemberPassWord()))
                .switchIfEmpty(Mono.just(false));

    }
}
