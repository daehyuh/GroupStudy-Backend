package com.example.demo.service;

import com.example.demo.domain.Authority;
import com.example.demo.domain.MemberEntity;
import com.example.demo.dto.MemberDto;
import com.example.demo.repository.MemberRepository;
import com.example.demo.util.SecurityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

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
    public String encryptPassword(String password){
        return passwordEncoder.encode(password);
    }

    public boolean checkPassword(Optional<MemberEntity> memberEntity, String password){
        return passwordEncoder.matches(password, memberEntity.get().getPassword());
    }

    public Boolean save(MemberDto body){
        // 이메일에 해당하는 유저가 이미 존재하는지 확인
        Optional<MemberEntity> memberEntity = memberRepository.findByEmail(body.getEmail());

        // 이미 존재하는 유저라면 에러를 발생시킴
        if (memberEntity.isPresent()){
            return false;
        }

        Authority authority = Authority.builder().authorityName("ROLE_USER").build();

        MemberEntity newMember = MemberEntity.builder()
                .email(body.getEmail())
                .password(passwordEncoder.encode(body.getPassword()))
                .name(body.getName())
                .authorities(Collections.singleton(authority)
                ).activated(false)
                .build();

        memberRepository.save(newMember);
        return true;
    }

    public List<MemberEntity> findAll(){
       return memberRepository.findAll();
    }

    public Optional<MemberEntity> findByEmail(String memberEmail){
        return memberRepository.findByEmail(memberEmail);
    }

    public void updateMember(MemberEntity memberEntity){
        memberEntity.setActivated(true);
        memberRepository.save(memberEntity);
    }



    // 유저, 권한정보를 가져오는 메서드 1
    // Email을 기준으로 정보를 가져옴
    @Transactional(readOnly = true)
    public Optional<MemberEntity> getUserWithAuthorities(String Email) {
        return memberRepository.findOneWithAuthoritiesByEmail(Email);
    }

    // 유저, 권한정보를 가져오는 메서드 2
    // SecurityContext에 저장된 Email 정보만 가져옴
    @Transactional(readOnly = true)
    public Optional<MemberEntity> getMyUserWithAuthorities(){

        return SecurityUtil.getCurrentUsername()
                .flatMap(memberRepository::findOneWithAuthoritiesByEmail);
    }



}
