package com.example.post_hanghae.controller;


import com.example.post_hanghae.dto.LoginRequestDto;
import com.example.post_hanghae.dto.MsgResponseDto;
import com.example.post_hanghae.dto.SignupRequestDto;
import com.example.post_hanghae.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController //JSON 형식
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class UserController {
    // Http request를 받아서 Service로 넘겨주고 가져온 데이터들을 requestDto 파라미터로 보냄
    private final UserService userService;

    // 회원가입
    @PostMapping("/signup")
    public MsgResponseDto signup(@RequestBody @Valid SignupRequestDto signupRequestDto) {
        // @RequestBody 어노테이션을 사용하여 HTTP 요청 바디로 받는 경우에는, 이 Dto 객체에 @Valid 어노테이션을 추가하여 검사가 수행되도록 해야 함
        return userService.signup(signupRequestDto); // signupRequestDto에 넣어서 userService로 보냄
    }

    @ResponseBody
    @PostMapping("/login")
    public MsgResponseDto login(@RequestBody LoginRequestDto loginRequestDto, HttpServletResponse response) {
        return userService.login(loginRequestDto, response);
   }
}
