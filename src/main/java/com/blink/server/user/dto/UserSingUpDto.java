package com.blink.server.user.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserSingUpDto {
    public String userId;
    public String userPassWord;
    public String userName;
    public String userEmail;
    public String userTel;
    public String userStudentNumber;
    public String userRegDate; // yyyymmdd 형식으로 전달
    public String userBirthDate; // yyyymmdd 형식으로 전달
    public boolean userSex; // true men , false woman
}
