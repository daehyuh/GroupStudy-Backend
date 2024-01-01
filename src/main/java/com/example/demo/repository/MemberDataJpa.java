package com.example.demo.repository;

import com.example.demo.domain.MemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberDataJpa extends JpaRepository<MemberEntity,Long>, MemberRepository {

}
