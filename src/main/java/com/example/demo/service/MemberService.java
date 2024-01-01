package com.example.demo.service;

import com.example.demo.domain.MemberEntity;
import com.example.demo.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class MemberService {
    private MemberRepository memberRepository;
    private PasswordEncoder passwordEncoder;

    @Autowired
    MemberService(MemberRepository memberRepository, PasswordEncoder passwordEncoder){
        this.memberRepository = memberRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public MemberEntity save(MemberEntity member){
        return memberRepository.save(member);
    }

    public List<MemberEntity> findAll(){
       return memberRepository.findAll();
    }

    public MemberEntity findByMemberEmail(String memberEmail){
        return memberRepository.findByEmail(memberEmail);
    }

    public String encryptPassword(String password){
        return passwordEncoder.encode(password);
    }

    public boolean checkPassword(MemberEntity memberEntity, String password){
        return passwordEncoder.matches(password, memberEntity.getPassword());
    }
}
