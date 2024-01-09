package com.example.demo.service;


import com.example.demo.domain.MemberEntity;
import com.example.demo.repository.MemberRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Component("userDetailsService")
public class CustomUserDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository;

    public CustomUserDetailsService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @Override // UserDetailsService 클래스의 loadUserByUsername 오버라이딩
    @Transactional
    public UserDetails loadUserByUsername(final String email) throws UsernameNotFoundException {
        return memberRepository.findOneWithAuthoritiesByEmail(email)// 로그인 시 DB 유저정보와 권한정보를 가져옴
                .map(member -> createUser(email, member)) // 데이터베이스에서 가져온 정보를 기준으로 createUser 메서드 수행
                .orElseThrow(()-> new UsernameNotFoundException(email + "-> 데이터베이스에서 찾을 수 없습니다."));
    }

    private org.springframework.security.core.userdetails.User createUser(String username, MemberEntity member) {

        if (!member.getActivated()) {
            throw new RuntimeException(username+ "-> 활성화되어 있지 않습니다.");
        }
        // 해당 유저가 활성화 상태라면
        List<GrantedAuthority> grantedAuthorities = member.getAuthorities().stream() // getAuthorities() : 유저의 권한정보
                .map(authority -> new SimpleGrantedAuthority(authority.getAuthorityName())) //
                .collect(Collectors.toList());

        return new org.springframework.security.core.userdetails.User(member.getEmail(),  // 이메일
                member.getPassword(),  // 비밀번호를 가진
                grantedAuthorities); // 유저 객체를 리턴
    }

}
