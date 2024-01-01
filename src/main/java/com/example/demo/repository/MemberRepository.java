package com.example.demo.repository;

import com.example.demo.domain.MemberEntity;

import java.util.List;

public interface MemberRepository {

    MemberEntity save(MemberEntity member); // 회원가입
    List<MemberEntity> findAll(); // 전체 회원 조회

    MemberEntity findByEmail(String Email); // 아이디로 회원 조회

}