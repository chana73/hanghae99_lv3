package com.example.post_hanghae.entity;

import com.example.post_hanghae.dto.CommentRequestDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import jakarta.persistence.*;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Comment extends TimeStamped{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    private String contents;

    @ManyToOne
    @JoinColumn(name = "posts_id",nullable = false)
    private Post post;


    // 댓글 작성
    public Comment(CommentRequestDto commentRequestDto) {

        this.contents = commentRequestDto.getContents();
    }

    // ID 확인용
    public void setUsername(String username){

        this.username = username;
    }

    // 댓글 수정
    public void updatecomment(CommentRequestDto commentRequestDto) {

        this.contents = commentRequestDto.getContents();
    }

}
