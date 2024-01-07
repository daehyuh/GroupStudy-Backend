package com.example.demo.repository;

import com.example.demo.domain.MemberEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<MemberEntity, Long> {
    @EntityGraph(attributePaths = "authorities") // 쿼리 수행 시 Lazy 조회가 아닌, Eager 조회로 authorities 정보를 같이가져옴
    Optional<MemberEntity> findOneWithAuthoritiesByEmail(String Email);

    MemberEntity save(MemberEntity member); // 회원가입

    List<MemberEntity> findAll(); // 전체 회원 조회

    Optional<MemberEntity> findByEmail(String Email); // 이메일로 회원 조회

}
