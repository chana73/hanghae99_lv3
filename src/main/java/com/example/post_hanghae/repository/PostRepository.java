package com.example.post_hanghae.repository;

import com.example.post_hanghae.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {

    List<Post> findAllByOrderByModifiedAtDesc(); //등록된 순 내림차순

}