package com.example.post_hanghae.service;

import com.example.post_hanghae.dto.*;
import com.example.post_hanghae.entity.Comment;
import com.example.post_hanghae.entity.Post;
import com.example.post_hanghae.entity.User;
import com.example.post_hanghae.entity.UserRoleEnum;
import com.example.post_hanghae.jwt.JwtUtil;
import com.example.post_hanghae.repository.CommentRepository;
import com.example.post_hanghae.repository.PostRepository;
import com.example.post_hanghae.repository.UserRepository;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.util.StringUtils;


@Service
@RequiredArgsConstructor
public class CommentService {

    public static final String SUBJECT_KEY = "sub";
    public static final String AUTHORIZATION_KEY = "auth";
    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    // 댓글 작성
    @Transactional
    public CommentResponseDto createComment(Long id, CommentRequestDto commentRequestDto, HttpServletRequest request) {

        Post post = postRepository.findById(id).orElseThrow(
                () -> new NullPointerException("존재하지 않는 게시글입니다.")
        );

        Comment comment = new Comment(commentRequestDto);

        comment.setPost(post);

        String token = jwtUtil.resolveToken(request); //jwt 안에 있는 정보를 담는 claims 객체
        Claims claims;

        if (token == null) {
            return null;
        }
        if (!jwtUtil.validateToken(token)) {
            throw new IllegalArgumentException("토큰이 유효하지 않습니다.");
        }
        claims = jwtUtil.getUserInfoFromToken(token);
        User user = userRepository.findByUsername(claims.getSubject()).orElseThrow(
                () -> new IllegalArgumentException("회원을 찾을 수 없습니다.")
        );
        comment.setUsername(user.getUsername());

        commentRepository.saveAndFlush(comment);
        return new CommentResponseDto(comment);
    }

    // 수정
    @Transactional
    public CommentResponseDto updateComment(Long id, CommentRequestDto commentRequestDto, HttpServletRequest request) {

        Comment comment = commentRepository.findById(id).orElseThrow(
                () -> new NullPointerException("존재하지 않는 게시글입니다.")
        );

        String token = jwtUtil.resolveToken(request); //jwt 안에 있는 정보를 담는 claims 객체
        Claims claims;

        if (token == null) {
            return null;
        }
        if (!jwtUtil.validateToken(token)) {
            throw new IllegalArgumentException("토큰이 유효하지 않습니다.");
        }
        claims = jwtUtil.getUserInfoFromToken(token);
        String username = claims.get(SUBJECT_KEY, String.class);
        String role = claims.get(AUTHORIZATION_KEY, String.class);

        if (StringUtils.equals(role, UserRoleEnum.USER.name())) {
            if(!StringUtils.equals(comment.getUsername(), username)) {
                throw new IllegalArgumentException("회원을 찾을 수 없습니다.");
            } else {
                comment.updatecomment(commentRequestDto);
                return new CommentResponseDto(comment);
            }
        }
        comment.updatecomment(commentRequestDto);
        return new CommentResponseDto(comment);
    }

    // 댓글 삭제
    @Transactional
    public MsgResponseDto deleteComment(Long id, HttpServletRequest request) {

        Comment comment = commentRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException("존재하지 않는 게시글입니다.")
        );

        String token = jwtUtil.resolveToken(request);
        Claims claims;
        if (token == null) {
            return null;
        }
        if (!jwtUtil.validateToken(token)) {
            return  new MsgResponseDto("토큰이 유효하지 않습니다.", HttpStatusCode.valueOf(400));
        }
        claims = jwtUtil.getUserInfoFromToken(token);
        String username = claims.get(SUBJECT_KEY, String.class);
        String role = claims.get(AUTHORIZATION_KEY, String.class);

        if(StringUtils.equals(role, UserRoleEnum.USER.name())) {
            if (!StringUtils.equals(comment.getUsername(), username)) {
                return  new MsgResponseDto("아이디가 같지 않습니다.", HttpStatusCode.valueOf(400));
            } else {
                commentRepository.delete(comment);
                return new MsgResponseDto("댓글 삭제 성공", HttpStatusCode.valueOf(200));
            }
        }
        commentRepository.delete(comment);
        return new MsgResponseDto("댓글 삭제 성공", HttpStatusCode.valueOf(200));
    }
}
