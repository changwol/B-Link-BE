package com.blink.server.user.dto;

import com.blink.server.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserSingUpDto {
    private String userId;
    private String userPassWord;
    private String userName;
    private String userEmail;
    private String userTel;
    private String userStudentNumber;
    private String userRegDate; // yyyymmdd 형식으로 전달
    private String userBirthDate; // yyyymmdd 형식으로 전달
    private boolean userSex; // true for men, false for woman

    public static UserSingUpDto of(User user) {
        return new UserSingUpDto(
                user.getUserId(),
                user.getUserPassWord(),
                user.getUserName(),
                user.getUserEmail(),
                user.getUserTel(),
                user.getUserStudentNumber(),
                user.getUserRegDate(),
                user.getUserBirthDate(),
                user.isUserSex()
        );
    }
}
