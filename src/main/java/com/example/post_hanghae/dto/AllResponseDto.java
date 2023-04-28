package com.example.post_hanghae.dto;

import com.example.post_hanghae.entity.Post;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class AllResponseDto {
    // 필드 값
    private Long id;
    private String username; //final - 한번 정하면 절대 변하지 않음 (상수) 왜 final 쓰면 빨간줄이지?
    private String title;
    private String content;
    private LocalDateTime createAt;
    private LocalDateTime modifiedAt;
    private List<CommentResponseDto> comments;

    //생성자
    public AllResponseDto(Post post, List<CommentResponseDto> comments) { // -> 내부에서 생성자만 건들이도록 public 해줌
        this.id = post.getId();
        this.username = post.getUsername();
        this.title = post.getTitle();
        this.content = post.getContent();
        this.createAt = post.getCreatedAt();
        this.modifiedAt = post.getModifiedAt();
        this.comments = comments;
    }


}
