package com.blink.server.user.dto;

import com.blink.server.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

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
    private List<String> roomIds; // 여러 개의 roomId를 저장할 수 있는 필드 추가

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
                user.isUserSex(),
                user.getRoomIds() // User 엔티티에서 roomIds 가져오기
        );
    }
}
