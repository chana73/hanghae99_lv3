package com.example.post_hanghae.service;

import com.example.post_hanghae.dto.LoginRequestDto;
import com.example.post_hanghae.dto.MsgResponseDto;
import com.example.post_hanghae.dto.SignupRequestDto;
import com.example.post_hanghae.entity.User;
import com.example.post_hanghae.entity.UserRoleEnum;
import com.example.post_hanghae.jwt.JwtUtil;
import com.example.post_hanghae.repository.UserRepository;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private static final String ADMIN_TOKEN = "AAABnvxRVklrnYxKZ0aHgTBcXukeZygoC";

    @Transactional
    public MsgResponseDto signup(SignupRequestDto signupRequestDto) {
        String username = signupRequestDto.getUsername();
        String password = signupRequestDto.getPassword();

        // 회원 중복 확인
        Optional<User> found = userRepository.findByUsername(username);
        if (found.isPresent()) {
            return new MsgResponseDto("중복된 username 입니다.", HttpStatusCode.valueOf(400));
        }
        // SignupRequestDto에 적어줌
//        if(!Pattern.matches("^[a-z0-9]{4,10}$", username) || !Pattern.matches("^[a-zA-Z\\p{Punct}0-9]*$", password)){
////            throw new IllegalStateException("회원가입 양식 조건에 맞지 않습니다.");
//            return new MsgResponseDto("회원가입 양식 조건에 맞지 않습니다.", HttpStatus.BAD_REQUEST);
//        }

        // 사용자 ROLE 확인
        UserRoleEnum role = UserRoleEnum.USER;
        if (signupRequestDto.isAdmin()) {
            if(!signupRequestDto.getAdminToken().equals(ADMIN_TOKEN)) {
              return new MsgResponseDto("관리자 암호가 틀려 등록이 불가능합니다.", HttpStatusCode.valueOf(400));
            }
            role = UserRoleEnum.ADMIN;
        }

        User user = new User(username, password, role);

        userRepository.save(user);
        return new MsgResponseDto("회원가입 성공", HttpStatusCode.valueOf(200));
    }

    @Transactional(readOnly = true)
    public MsgResponseDto login(LoginRequestDto loginRequestDto, HttpServletResponse response) {
        String username = loginRequestDto.getUsername();
        String password = loginRequestDto.getPassword();

        //사용자 확인
        User user = userRepository.findByUsername(username).orElseThrow(
                () -> new IllegalArgumentException("등록된 사용자가 없습니다.")
        );

        // 비밀번호 확인
        if(!user.getPassword().equals(password)) {
            return new MsgResponseDto("비밀번호가 틀립니다..", HttpStatusCode.valueOf(400));
        }

        response.addHeader(JwtUtil.AUTHORIZATION_HEADER, jwtUtil.createToken(user.getUsername(), user.getRole()));
        return new MsgResponseDto("로그인 성공", HttpStatusCode.valueOf(200));

    }
}
