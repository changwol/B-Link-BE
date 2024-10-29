package com.blink.server.member.dto;

import com.blink.server.member.entity.Member;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MemberSingUpDto {
    private String userId;
    private String userPassWord;
    private String userName;
    private String userEmail;
    private String userTel;
    private String userStudentNumber;
    private String userRegDate; // yyyymmdd 형식으로 전달
    private String userBirthDate; // yyyymmdd 형식으로 전달
    private boolean userSex; // true for men, false for woman

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
                member.isMemberSex()
        );
    }
}
