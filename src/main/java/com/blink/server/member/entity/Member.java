package com.blink.server.member.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.math.BigInteger;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Document(collection = "members")
@Getter
@Setter
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Member implements UserDetails {
    @Id
    private String memberCode;
    private String memberId;
    private String memberPassWord;
    private String memberName;
    private String memberEmail;
    private String memberTel;
    private List<String> memberRoles; // USER_STUDENT , USER_PROFESSOR, ADMIN
    private String memberStudentNumber;
    private String memberRegDate;
    private String memberBirthDate;
    private boolean memberSex; // 1 man , 0 woman
    @Getter
    private List<String> roomIds;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.memberRoles.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }

    @Override
    public String getPassword() {
        return this.memberPassWord;
    }

    @Override
    public String getUsername() {
        return this.memberId;
    }

}
