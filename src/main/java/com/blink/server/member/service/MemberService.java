package com.blink.server.member.service;

import com.blink.server.chat.event.RoomIdChangeEvent;
import com.blink.server.jwt.JwToken;
import com.blink.server.jwt.JwTokenProvider;
import com.blink.server.member.dto.MemberInfoDto;
import com.blink.server.member.dto.MemberSingUpDto;
import com.blink.server.member.entity.Member;
import com.blink.server.member.repository.MemberRepository;

//import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.DirectProcessor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxProcessor;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@RequiredArgsConstructor
@Service
public class MemberService {
    private static final Logger logger = LoggerFactory.getLogger(MemberService.class);

    private final MemberRepository memberRepository;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final JwTokenProvider jwTokenProvider;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    private final FluxProcessor<RoomIdChangeEvent, RoomIdChangeEvent> roomIdChangeEventPublisher =
            DirectProcessor.create();


    public boolean canUseThisUserId(String userId) {
        return true; // DB 조회해서 구현 필요
    }

    /**
     * 회원가입
     *
     * @param dto 회원가입 위한 DTO
     * @return
     */
    LocalDate today = LocalDate.now();

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
    String formattedDate = today.format(formatter);

    public Mono<Member> saveUser(MemberSingUpDto dto) {
        Member member = new Member();
        member.setMemberId(dto.getMemberId());
        member.setMemberPassWord(bCryptPasswordEncoder.encode(dto.getMemberPassWord()));
        member.setMemberName(dto.getMemberName());
        member.setMemberEmail(dto.getMemberEmail());
        member.setMemberTel(dto.getMemberTel());
        member.setMemberStudentNumber(String.valueOf(dto.getMemberStudentNumber()));
        member.setMemberRegDate(formattedDate);
        member.setMemberBirthDate(dto.getMemberBirthDate());
        member.setRoomIds(Collections.singletonList(dto.getRoomIds().toString()));
        member.setMemberSex(dto.isMemberSex());
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
                member.getMemberBirthDate()
        );

        return memberRepository.save(member);
    }

    /**
     * 로그인 하는 메서드, 로그인 성공시 JWT 토큰 반환
     *
     * @param userName user ID
     * @param password user PassWord
     * @return JWT 토큰 반환
     */
    public JwToken loginMember(String userName, String password) {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userName, password);

        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

        JwToken jwToken = jwTokenProvider.generateToken(authentication);
        return jwToken;
    }

    public Mono<Boolean> isThisPasswordMatch(String memberId, String memberPassword) {
        Mono<Member> findMember = memberRepository.findByMemberId(memberId);
        return findMember.map(member -> bCryptPasswordEncoder.matches(memberPassword, member.getMemberPassWord()))
                .switchIfEmpty(Mono.just(false));

    }

    public Mono<MemberInfoDto> getMemberInfomation(String memberId) {
        Mono<Member> findMember = memberRepository.findByMemberId(memberId);
        return findMember.map(member ->
                        MemberInfoDto.builder()
                                .memberCode(member.getMemberCode())
                                .memberId(member.getMemberId())
                                .memberName(member.getMemberName())
                                .memberEmail(member.getMemberEmail())
                                .memberTel(member.getMemberTel())
                                .memberStudentNumber(member.getMemberStudentNumber())
                                .memberRegDate(member.getMemberRegDate())
                                .memberBirthDate(member.getMemberBirthDate())
                                .memberSex(member.isMemberSex())
                                .build())
                .switchIfEmpty(Mono.empty());
    }

    public Mono<List<String>> getRoomIds(String memberId) {
        return memberRepository.findByMemberId(memberId) // memberId로 Member 찾기
                .map(Member::getRoomIds) // Member 객체에서 roomIds 가져오기
                .defaultIfEmpty(Collections.emptyList()); // Member가 없을 경우 빈 리스트 반환
    }

    public Flux<List<String>> getRoomIdsFlux(String memberId) {
        return roomIdChangeEventPublisher
                .filter(changeEvent -> changeEvent.getMemberId().equals(memberId)) // 특정 멤버의 이벤트 필터링
                .flatMap(changeEvent -> memberRepository.findByMemberId(memberId) // 변경된 멤버의 방 ID 목록 가져오기
                        .map(Member::getRoomIds)); // 방 ID 목록 반환
    }

    public Mono<Void> addRoomIdToMember(String memberId, Mono<String> roomIdMono) {
        return memberRepository.findByMemberId(memberId)
                .flatMap(member -> {
                    // 방 ID를 Mono로 받아서 사용
                    return roomIdMono.map(roomId -> {
                                // 방 ID를 List에 추가
                                member.getRoomIds().add(roomId);
                                return member;
                            })
                            .flatMap(updatedMember -> memberRepository.save(updatedMember)); // 업데이트된 멤버 저장
                })
                .then(); // Mono<Void> 반환
    }
//    백업 public Mono<Void> addRoomIdToMember(String memberId, Mono<String> roomIdMono) {
//        return memberRepository.findByMemberId(memberId)
//                .flatMap(member -> {
//                    // 방 ID를 Mono로 받아서 사용
//                    return roomIdMono.map(roomId -> {
//                                // 방 ID를 List에 추가
//                                member.getRoomIds().add(roomId);
//                                return member;
//                            })
//                            .flatMap(updatedMember -> memberRepository.save(updatedMember)); // 업데이트된 멤버 저장
//                })
//                .then(); // Mono<Void> 반환
//    }
//    public Mono<Void> addRoomIdToMember(String memberId, Mono<String> roomId) {
//        Mono<List<String>> tmp=memberRepository.findByRoomIds(memberId).map(Member::getRoomIds);
//        System.out.println(tmp);
//        memberRepository.findByMemberId(memberId)
//                .doOnNext(member -> {
//                    System.out.println("do on next"+member.getRoomIds());
//                    member.getRoomIds().add(String.valueOf(roomId)); // 방 ID 추가
//                    memberRepository.save(member); // 업데이트된 멤버 저장
//                });
//        return null;
//    }

    void updateRoomIdToMember(String memberId, String roomId) {
        memberRepository.findByMemberId(memberId).map(Member::getRoomIds);
    }

    public Mono<String> findMemberById(String userId) {
        return memberRepository.findByMemberId(userId).map(Member::getMemberName);
    }

    public Flux<Object> getMemberName(String memberId) {
        return memberRepository.findByMemberName(memberId).map(Member::getMemberName);
    }

    public Flux<Member> getMemberNameList(String memberName) {
        return memberRepository.findByMemberNameContainingIgnoreCase(memberName);
    }
}
