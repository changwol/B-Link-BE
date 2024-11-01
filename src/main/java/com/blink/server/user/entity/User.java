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
import java.util.List;

@Document(collection = "users")
@Getter
@Setter
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class User {
    @Id
    private String id; // MongoDB에서 자동 생성되는 고유 ID
    private String userId; // 사용자 ID
    private String userPassWord; // 사용자 비밀번호
    private String userName; // 사용자 이름
    private String userEmail; // 사용자 이메일
    private String userTel; // 사용자 전화번호
    private String userStudentNumber; // 학번
    private String userRegDate; // 등록일
    private String userBirthDate; // 생년월일
    private boolean userSex; // 성별
    private List<String> roomIds; // 여러 개의 roomId

}
