package com.example.demo.controller;


import com.example.demo.api.ApiResponseDto;
import com.example.demo.domain.MemberEntity;
import com.example.demo.dto.LoginDto;
import com.example.demo.dto.TokenDto;

import com.example.demo.service.MemberService;
import com.example.demo.service.jwt.JwtFilter;
import com.example.demo.service.jwt.TokenProvider;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Tag(name = "AuthController", description = "AuthController API")
@RestController
@RequestMapping("/v1")
public class AuthController {

    private final TokenProvider tokenProvider;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final MemberService memberService;

    public AuthController(TokenProvider tokenProvider,
                          AuthenticationManagerBuilder authenticationManagerBuilder, MemberService memberService) {
        this.tokenProvider = tokenProvider;
        this.authenticationManagerBuilder = authenticationManagerBuilder;
        this.memberService = memberService;
    }

    // hello world 출력하는 api
    @Operation(operationId = "hello", summary = "hello", description = "hello", tags = "AuthController")
    @PostMapping("/hello")
    @ResponseBody
    public String hello() {
    return "hello world";
    }


    @Operation(operationId = "authenticate", summary = "로그인", description = "요청을 검토한뒤 로그인", tags = "AuthController")
    @PostMapping("/authenticate")
    public ResponseEntity<ApiResponseDto<MemberEntity>> authorize(@Valid @RequestBody LoginDto loginDto) {

        Optional<MemberEntity> member = memberService.findByEmail(loginDto.getEmail());

        if (member.isEmpty()) {
            return ResponseEntity.ok(
                    ApiResponseDto.success(
                            HttpStatus.BAD_REQUEST,
                            null
                    ));
        }

        if (!member.get().getActivated()){
            return ResponseEntity.ok(
                    ApiResponseDto.success(
                            HttpStatus.BAD_REQUEST,
                            null
                    ));
        }

        if (!memberService.checkPassword(member, loginDto.getPassword())){
            return ResponseEntity.ok(
                    ApiResponseDto.success(
                            HttpStatus.BAD_REQUEST,
                            null
                    ));
        }


        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                loginDto.getEmail(), loginDto.getPassword());

        Authentication authentication = authenticationManagerBuilder.getObject()
                .authenticate(authenticationToken);

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwt = tokenProvider.createToken(authentication);

        System.out.println("jwt = " + jwt);

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(JwtFilter.AUTHORIZATION_HEADER, "Bearer " + jwt);

        return ResponseEntity.ok(
                ApiResponseDto.success(
                        HttpStatus.OK,
                        new TokenDto(jwt)
                ));
    }
}
