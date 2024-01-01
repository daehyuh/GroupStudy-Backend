package com.example.demo.controller;


import com.example.demo.api.ApiResponseDto;
import com.example.demo.controller.request.MemberRequest;
import com.example.demo.domain.MemberEntity;
import com.example.demo.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Tag(name = "MemberController", description = "MemberController API")
@Controller
public class MemberController {
    private MemberService memberService;
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    MemberController(MemberService memberService){
        this.memberService = memberService;
    }

    @Operation(operationId = "join", summary = "회원가입", description = "요청을 검토한뒤 회원가입", tags = "MemberController")
    @PostMapping("/v1/join")
    public ResponseEntity<ApiResponseDto<MemberEntity>> join(
            @RequestBody MemberRequest body
    ){
        if (memberService.findByMemberEmail(body.getEmail()) != null) {
            return ResponseEntity.ok(
                    ApiResponseDto.success(
                            HttpStatus.BAD_REQUEST
                            )
                    );
        }

        return ResponseEntity.ok(
                ApiResponseDto.success(
                        HttpStatus.CREATED
                        , memberService.save(
                                new MemberEntity(
                                        body.getName(),
                                        body.getEmail(),
                                        memberService.encryptPassword(body.getPassword())
                                )
                        )
                )
        );
    }

    @Operation(operationId = "member", summary = "회원조회", description = "요청을 검토한뒤 회원조회", tags = "MemberController")
    @GetMapping("/v1/members")
    @ResponseBody
    public List<MemberEntity> members(){
        return memberService.findAll();
    }

    //로그인 구현
    @Operation(operationId = "login", summary = "로그인", description = "요청을 검토한뒤 로그인", tags = "MemberController")
    @PostMapping("/v1/login")
    public ResponseEntity<ApiResponseDto<MemberEntity>> login(@RequestParam String email, @RequestParam String password) {
        MemberEntity member = memberService.findByMemberEmail(email);
        if (member == null) {
            return ResponseEntity.ok(
                    ApiResponseDto.success(
                            HttpStatus.BAD_REQUEST,
                            null
                    ));
        }
        if (!memberService.checkPassword(member, password)){
            return ResponseEntity.ok(
                    ApiResponseDto.success(
                            HttpStatus.BAD_REQUEST,
                            null
                    ));
        }
        return ResponseEntity.ok(
                ApiResponseDto.success(
                        HttpStatus.OK,
                        member
                ));
    }

}