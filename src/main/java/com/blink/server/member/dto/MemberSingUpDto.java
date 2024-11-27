package com.blink.server.member.dto;

import com.blink.server.member.entity.Member;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class MemberSingUpDto {
    private String memberId;
    private String memberPassWord;
    private String memberName;
    private String memberEmail;
    private String memberTel;
    private String memberStudentNumber;
    private String memberRegDate; // yyyymmdd 형식으로 전달
    private String memberBirthDate; // yyyymmdd 형식으로 전달
    private boolean memberSex; // true for men, false for woman
    private List<String> roomIds;

    public static MemberSingUpDto of(Member member) {
        return new MemberSingUpDto(
                member.getMemberId(),
                member.getMemberPassWord(),
                member.getMemberName(),
                member.getMemberEmail(),
                member.getMemberTel(),
                member.getMemberStudentNumber(),
                member.getMemberRegDate(),
                member.getMemberBirthDate(),
                member.isMemberSex(),
                member.getRoomIds()
        );
    }
}
