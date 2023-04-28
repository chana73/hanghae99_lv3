package com.example.post_hanghae.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor //파라미터 없는 기본생성자 만들어줌

public class PostRequestDto {

    private String title;
    private String content;

}