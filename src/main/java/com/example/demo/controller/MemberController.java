package com.example.demo.controller;


import com.example.demo.api.ApiResponseDto;
import com.example.demo.domain.MemberEntity;
import com.example.demo.dto.MemberDto;
import com.example.demo.service.EmailService;
import com.example.demo.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Tag(name = "MemberController", description = "MemberController API")
@RequestMapping("/v1")
@Controller
public class MemberController {
    private MemberService memberService;
    private EmailService emailService;

    @Autowired
    MemberController(MemberService memberService, EmailService emailService){
        this.memberService = memberService;
        this.emailService = emailService;
    }

    @Operation(operationId = "join", summary = "회원가입", description = "요청을 검토한뒤 회원가입", tags = "MemberController")
    @PostMapping("/join")
    public ResponseEntity<ApiResponseDto<MemberEntity>> join(
            @RequestBody MemberDto body
    ) throws Exception {

        Boolean status = memberService.save(body);
        if (status == false){
            return ResponseEntity.ok(
                    ApiResponseDto.success(
                            HttpStatus.BAD_REQUEST,
                            "이미 존재하는 이메일입니다."
                    ));
        } else {
            emailService.createMessage(body.getEmail());
            return ResponseEntity.ok(
                    ApiResponseDto.success(
                            HttpStatus.CREATED
                            , "회원가입에 성공하였습니다."
                    )
            );
        }
    }

    @Operation(operationId = "findAll", summary = "모든회원조회", description = "요청을 검토한뒤 회원조회", tags = "MemberController")
    @GetMapping("/members")
    @PreAuthorize("hasAnyRole('ADMIN')")
    @ResponseBody
    public List<MemberEntity> members(){
        return memberService.findAll();
    }

    //이메일을 통하여 회원정보 조회
    @Operation(operationId = "findByEmail", summary = "이메일로 조회", description = "요청을 검토한뒤 회원조회", tags = "MemberController")
    @GetMapping("/email")
    @PreAuthorize("hasAnyRole('ADMIN')")
    @ResponseBody
    public ResponseEntity<ApiResponseDto<MemberEntity>> member(@RequestParam String email){
        Optional<MemberEntity> member = memberService.findByEmail(email);
        if (member.isEmpty()){
            return ResponseEntity.ok(
                    ApiResponseDto.success(
                            HttpStatus.BAD_REQUEST,
                            null
                    ));
        }
        return ResponseEntity.ok(
                ApiResponseDto.success(
                        HttpStatus.OK,
                        member.get()
                ));
    }

    @Operation(operationId = "findByMe", summary = "나의 정보 조회", description = "저장된 토큰을 통해  회원조회", tags = "MemberController")
    @GetMapping("/user")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<MemberEntity> getMyUserInfo(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(memberService.getMyUserWithAuthorities().get());
    }


    @Operation(operationId = "checkCertificationMail", summary = "이메일 인증번호 확인", description = "요청을 검토한뒤 이메일 인증번호 확인", tags = "EmailController")
    @GetMapping("/email/certification/check")
    @ResponseBody
    public ResponseEntity<ApiResponseDto<MemberEntity>> checkCertificationMail(@RequestParam String code, @RequestParam String email){
        try{
            if (emailService.getData(code).equals(email)){
                emailService.deleteData(code);

                MemberEntity member = memberService.findByEmail(email).get();

                memberService.updateMember(member); // false to true

                return ResponseEntity.ok(
                        ApiResponseDto.success(
                                HttpStatus.OK
                                , "인증에 성공하였습니다."
                        )
                );
            }else{
                return ResponseEntity.ok(
                        ApiResponseDto.success(
                                HttpStatus.INTERNAL_SERVER_ERROR
                                , "인증에 실패하였습니다."
                        )
                );
            }
        }catch (Exception exception){
            exception.printStackTrace();
            return ResponseEntity.ok(
                    ApiResponseDto.success(
                            HttpStatus.INTERNAL_SERVER_ERROR
                            , "인증에 실패하였습니다."
                    )
            );
        }
    }

}