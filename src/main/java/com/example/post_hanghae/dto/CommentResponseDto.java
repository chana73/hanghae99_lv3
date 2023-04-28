package com.example.post_hanghae.dto;

import com.example.post_hanghae.entity.Comment;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class CommentResponseDto {

    private Long id;
    private String username;
    private String content;
    private LocalDateTime createAt;
    private LocalDateTime modifiedAt;


    public CommentResponseDto(Comment comment) {
        this.id = comment.getId();
        this.username = comment.getUsername();
        this.content = comment.getContents();
        this.createAt = comment.getCreatedAt();
        this.modifiedAt = comment.getModifiedAt();

    }
}
