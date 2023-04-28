package com.example.post_hanghae.repository;

import com.example.post_hanghae.dto.CommentResponseDto;
import com.example.post_hanghae.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    @Query("select c from Comment c where c.post.id = :id order by c.modifiedAt desc")
    List<CommentResponseDto> findCommentsByPostId(@Param("id") Long id);

    void deleteByPost_Id(Long id); // 게시물 삭제시 댓글같이 삭제
}
