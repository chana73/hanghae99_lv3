package com.example.post_hanghae.controller;

import com.example.post_hanghae.dto.CommentRequestDto;
import com.example.post_hanghae.dto.CommentResponseDto;
import com.example.post_hanghae.dto.MsgResponseDto;
import com.example.post_hanghae.service.CommentService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class CommentController {

    private final CommentService commentService;


    // 댓글 등록
    @PostMapping("/post/{id}/comment")
    public CommentResponseDto createComment(@PathVariable Long id, @RequestBody CommentRequestDto commentRequestDto, HttpServletRequest request) {
        return commentService.createComment(id, commentRequestDto, request);
    }

    // 댓글 수정
    @PutMapping("/put/comment/{id}")
    public CommentResponseDto updateComment(@PathVariable Long id, @RequestBody CommentRequestDto commentRequestDto, HttpServletRequest request) {
        return commentService.updateComment(id, commentRequestDto, request);
    }


    // 댓글 삭제
    @DeleteMapping("/delete/comment/{id}")
    public MsgResponseDto deleteComment(@PathVariable Long id, HttpServletRequest request) {
        return commentService.deleteComment(id, request);
    }

}
