package com.blink.server.member.custom;

import com.blink.server.member.entity.Member;
import com.blink.server.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class CustomMemberDetailsService implements UserDetailsService {
    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return memberRepository.findByMemberId(username)
                .map(this::createUserDetails) // UserDetails로 변환
                .switchIfEmpty(Mono.error(new UsernameNotFoundException("해당하는 회원을 찾을 수 없습니다."))) // 예외 처리
                .block(); // 결과를 동기적으로 가져옵니다.
    }


    public UserDetails createUserDetails(Member member) {
        return User.builder()
                .username(member.getMemberId())
                .password(member.getMemberPassWord())
                .roles(member.getMemberRoles().toArray(new String[0]))
                .build();
    }
}
