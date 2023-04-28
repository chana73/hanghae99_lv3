package com.example.post_hanghae.jwt;

import com.example.post_hanghae.entity.UserRoleEnum;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SecurityException;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.security.Key;
import java.util.Base64;
import java.util.Date;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtUtil { //빈 등록 확인 가능(나뭇잎모양)

    //필드 - 토큰 생성에 필요한 값
    public static final String AUTHORIZATION_HEADER = "Authorization"; //Bearer<JWT>에서 Heaer에 들어가는 key값

    public static final String AUTHORIZATION_KEY = "auth"; // 사용자 권한 갑
    private static final String BEARER_PREFIX = "Bearer "; //토큰 식별자 (토큰을 만들때 맨앞에 들어감)
    private static final long TOKEN_TIME = 60 * 60 * 1000L; //토큰만료시간 : ms(밀리세컨드)기준. 60x1000L= 1분, 60X60 = 1시간

    @Value("${jwt.secret.key}") //@Value("${프로퍼티 키값}") : application.properties에서 적은 내용 가져와서 사용
    private String secretKey; //@Value 안에 applicationproperties에 넣어둔 key값
    private Key key; // 토큰을 만들때 넣어줄 key값
    private final SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;


    // method
    @PostConstruct
    public void init() {
        byte[] bytes = Base64.getDecoder().decode(secretKey);
        key = Keys.hmacShaKeyFor(bytes);
    }

    // header 토큰을 가져오기
    public String resolveToken(HttpServletRequest request) { //HttpServletRequest reauest 객체 안에 있는 토큰 값 가져옴
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) { // 토큰 유무 검사 (BEARER_PREFIX로 시작하는지?)
            return bearerToken.substring(7); // 앞부분은 토큰과 관계없는 부분이라 지우기 위해 substring사용
        }
        return null;
    }

    // 토큰 생성
    public String createToken(String username, UserRoleEnum role) {
        Date date = new Date();

        return BEARER_PREFIX +
                Jwts.builder() //여기서 실제로 만들어짐
                        .setSubject(username) //setSubject안에 username 넣기
                        .claim(AUTHORIZATION_KEY, role) //calim안에 AUTHORIZATION_KEY 사용자 권한 넣기
                        .setExpiration(new Date(date.getTime() + TOKEN_TIME)) //토큰 유효 시간
                        .setIssuedAt(date) //토큰 언제 만들어짐?
                        .signWith(key, signatureAlgorithm) //어떤 암호화 알고리즘을 사용해서 암호화할건지?
                        .compact();
    }

    // 토큰 검증
    public boolean validateToken(String token) {
        try { // 검증방법,        토큰만들때 사용한 key,         어떤 토큰을 검증할건지
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (SecurityException | MalformedJwtException e) {
            log.info("Invalid JWT signature, 유효하지 않는 JWT 서명 입니다.");
        } catch (ExpiredJwtException e) {
            log.info("Expired JWT token, 만료된 JWT token 입니다.");
        } catch (UnsupportedJwtException e) {
            log.info("Unsupported JWT token, 지원되지 않는 JWT 토큰 입니다.");
        } catch (IllegalArgumentException e) {
            log.info("JWT claims is empty, 잘못된 JWT 토큰 입니다.");
        }
        return false;
    }

    // 토큰에서 사용자 정보 가져오기 -> 위에서 validateToken으로 검증했으니까 이 부분을 사용할 수 있음
    public Claims getUserInfoFromToken(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
    }

}