package com.blink.server.user.entity;

import com.blink.server.chat.dto.MessageDto;
import com.blink.server.chat.entity.Message;
import com.blink.server.user.dto.UserSingUpDto;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigInteger;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Document(collection = "users")
@Getter
@Setter
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class User {
    @Id
    private BigInteger userCode;
    private String userId;
    private String userPassWord;
    private String userName;
    private String userEmail;
    private String userTel;
    private UserRole userRole; // USER_STUDENT , USER_PROFESSOR, ADMIN
    private String userStudentNumber;
    private String userRegDate;
    private String userBirthDate;
    private boolean userSex; // 1 man , 0 woman

//    public User UserDtoToEntity(UserSingUpDto dto) {
//        this.userId = dto.getUserId();
//        this.userPassWord = dto.getUserPassWord();
//        this.userName = dto.getUserName();
//        this.userEmail = dto.getUserEmail();
//        this.userTel = dto.getUserTel();
//        this.userRole = UserRole.USER_STUDENT; // 학생이 기본값
//        this.userStudentNumber = dto.getUserStudentNumber();
//        this.userRegDate = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
//        this.userBirthDate = dto.getUserBirthDate();
//        this.userSex = dto.isUserSex();
//        return this;
//    }
}
