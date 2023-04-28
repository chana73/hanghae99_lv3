package com.example.post_hanghae.service;

import com.example.post_hanghae.dto.*;
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

import java.util.ArrayList;
import java.util.List;
@Service
@RequiredArgsConstructor //fianl이 붙거나 @NotNull이 붙은 필드의 생성자를 자동 생성
public class PostService {

    public static final String SUBJECT_KEY = "sub";
    public static final String AUTHORIZATION_KEY = "auth";
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;


    //@Autowired 를 안쓰는 이유 -> private "final"을 꼭 써야하기 때문!(안전)
    // 게시글 작성
    @Transactional
    public PostResponseDto createPost(PostRequestDto postRequestDto, HttpServletRequest request) {
        String token = jwtUtil.resolveToken(request); // JWT 안에 있ㄴ는 정보를 담는 clams 객체
        Claims claims;

        if (token == null) {
            return null;
        }
        if (!jwtUtil.validateToken(token)) {
            throw new IllegalArgumentException("토큰이 유효하지 않습니다.");
        }
        claims = jwtUtil.getUserInfoFromToken(token);

        User user = userRepository.findByUsername(claims.getSubject()).orElseThrow(
                () -> new IllegalArgumentException("사용자가 존재하지 않습니다.")
        );
//        String title = postRequestDto.getTitle();
//        String content = postRequestDto.getContent();
//        String username = user.getUsername();
//
//        Post newPost = new Post(title, content, username);
        Post post = postRepository.saveAndFlush(new Post(postRequestDto, user.getUsername()));

        return new PostResponseDto(post);
    }

    // 전체 게시글 목록 조회
    @Transactional(readOnly = true)
    public List<AllResponseDto> getPosts() {
        List<AllResponseDto> allResponseDto = new ArrayList<>();
        List<Post> posts = postRepository.findAllByOrderByModifiedAtDesc();

        for (Post post : posts) {
            List<CommentResponseDto> comments;
            comments = commentRepository.findCommentsByPostId(post.getId());
            allResponseDto.add(new AllResponseDto(post, comments));
        }

        return allResponseDto;
    }

    // 선택한 게시글 조회
    @Transactional(readOnly = true) 
    public AllResponseDto getPost(Long id) {
        Post post = postRepository.findById(id).orElseThrow(() -> new NullPointerException("존재하지 않는 게시글입니다."));

        List<CommentResponseDto> comments = commentRepository.findCommentsByPostId(id);

        return new AllResponseDto(post, comments);
    }

    // 게시글 수정
    @Transactional
    public PostResponseDto update(Long id, PostRequestDto postRequestDto, HttpServletRequest request) {
        Post post = postRepository.findById(id).orElseThrow(
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

        // RoleEnum과 username을 비교
        // StringUtils 찾아보기
        if (StringUtils.equals(role, UserRoleEnum.USER.name())) {
            if (!StringUtils.equals(post.getUsername(), username)) {
                throw new IllegalArgumentException("회원을 찾을 수 없습니다.");
            }else {
                post.update(postRequestDto);
                return new PostResponseDto(post);
            }
        }
        post.update(postRequestDto);
        return new PostResponseDto(post);
    }

    // 게시글 삭제
    @Transactional
    public MsgResponseDto deleteAll(Long id, HttpServletRequest request) {
        Post post = postRepository.findById(id).orElseThrow(
                () -> new NullPointerException("게시글이 존재하지 않습니다.")
        );
        String token = jwtUtil.resolveToken(request);
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
             if (!StringUtils.equals(post.getUsername(), username)) {
                 return new MsgResponseDto("아이디가 같지 않습니다.", HttpStatusCode.valueOf(400));
            } else {
                 commentRepository.deleteByPost_Id(post.getId()); // 게시물 삭제시 댓글 같이 삭제
                 postRepository.delete(post);
                 return new MsgResponseDto("삭제 성공", HttpStatusCode.valueOf(200));
            }
        }
        commentRepository.deleteByPost_Id(post.getId()); // 게시물 삭제시 댓글 같이 삭제
        postRepository.delete(post);
        return new MsgResponseDto("삭제 성공", HttpStatusCode.valueOf(200));
    }
}