package com.example.demo.domain;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)
@Entity(name="Member")
public class MemberEntity {
    @Id
    @Column
    @GeneratedValue(strategy= GenerationType.SEQUENCE, generator = "Member_seq")
    @SequenceGenerator(name="Member_seq", sequenceName = "Member_seq", initialValue = 0, allocationSize = 1)
    long id;
    @Column
    String name;
    @Column
    String email;
    @Column
    String password;

    @CreatedDate
    LocalDateTime created_at;
    @LastModifiedDate
    LocalDateTime updated_at;

    public MemberEntity(String name, String email, String password){
        this.name = name;
        this.email = email;
        this.password = password;
    }

    public MemberEntity() {

    }
}
