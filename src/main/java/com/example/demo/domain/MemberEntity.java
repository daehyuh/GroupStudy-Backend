package com.example.demo.domain;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity(name="Member")
public class MemberEntity {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy= GenerationType.SEQUENCE, generator = "Member_seq")
    @SequenceGenerator(name="Member_seq", sequenceName = "Member_seq", initialValue = 0, allocationSize = 1)
    long id;
    @Column(name = "name")
    String name;
    @Column(name = "email")
    String email;
    @Column(name = "password")
    String password;
    @Column(name = "activated")
    Boolean activated;

    @CreatedDate
    LocalDateTime created_at;
    @LastModifiedDate
    LocalDateTime updated_at;

    @ManyToMany
    @JoinTable(
            name = "User_Authority",
            joinColumns = {@JoinColumn(name = "id", referencedColumnName = "id")},
            inverseJoinColumns = {@JoinColumn(name = "authority_name", referencedColumnName = "authority_name")})
    private Set<Authority> authorities;

}