package com.example.post_hanghae.repository;

import com.example.post_hanghae.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    //회원 중복확인
    Optional<User> findByUsername(String username);
}
