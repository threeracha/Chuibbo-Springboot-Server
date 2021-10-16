/**
 * JwtTokenProvider
 * JWT 토큰 발급
 *
 * @author jy
 * @version 1.0
 * @see None
 */

package com.sriracha.ChuibboServer.common.jwt;

import com.sriracha.ChuibboServer.model.enumclass.Role;
import io.jsonwebtoken.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.util.Base64;
import java.util.Date;

@RequiredArgsConstructor
@Component
@Slf4j
public class JwtTokenProvider {

    private final StringRedisTemplate redisTemplate;

    @Value("${spring.jwt.secret}")
    private String secretKey;

    /**
     * 카카오에서 시행하는 정책 예시
     * accessToken 6시간~24시간
     * refreshToken 1달~
     */
    // TODO 토큰 유효시간 정책 정하기
    public final static long ACCESS_TOKEN_VALIDATION_SECOND = 1000L * 60 * 10;
    public final static long REFRESH_TOKEN_VALIDATION_SECOND = 1000L * 60 * 30 * 2;

    final static public String ACCESS_TOKEN_NAME = "accessToken";
    final static public String REFRESH_TOKEN_NAME = "refreshToken";

    private final UserDetailsService userDetailsService;

    // 객체 초기화, secretKey를 Base64로 인코딩한다.
    @PostConstruct
    protected void init() {
        secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
    }

    // JWT 토큰 생성
    public String generateToken(String type, Long userPk, Role roles, String userEmail, long expireTime) {
        // TODO setSubject의 역할
        Claims claims = Jwts.claims()
                .setSubject(type); // JWT payload 에 저장되는 정보단위
        // key , value
        claims.put("userPk", userPk);
        claims.put("roles", roles);
        claims.put("userEmail", userEmail);
        // TODO claims에 userEmail 넣기
        Date now = new Date();
        return Jwts.builder()
                .setHeaderParam("typ", "JWT")
                .setClaims(claims) // 정보 저장
                .setIssuedAt(now) // 토큰 발행 시간 정보
                .setExpiration(new Date(now.getTime() + expireTime)) // set Expire Time
                .signWith(SignatureAlgorithm.HS256, secretKey)  // 사용할 암호화 알고리즘과
                // signature 에 들어갈 secret값 세팅
                .compact();
    }

    // JWT access 토큰 생성
    public String createToken(Long userPk, Role roles, String userEmail) {
        return generateToken("access_token", userPk, roles, userEmail, ACCESS_TOKEN_VALIDATION_SECOND);
    }

    // JWT refresh 토큰 생성
    public String createRefreshToken(Long userPk, Role roles, String userEmail) {
        return generateToken("refresh_token",userPk, roles, userEmail, REFRESH_TOKEN_VALIDATION_SECOND);
    }

    // JWT 토큰에서 인증 정보 조회
    public Authentication getAuthentication(String token) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(this.getUserEmail(token));
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    // 토큰에서 회원 정보 추출
    public Claims extractAllClaims(String token) throws ExpiredJwtException {
        return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody();
    }

    // 회원 primary key 가져오기
    public String getUserPk(String token) {
        return extractAllClaims(token).get("userPk", String.class);
    }

    // 회원 이메일 가져오기
    public String getUserEmail(String token) {
        return extractAllClaims(token).get("userEmail", String.class);
    }

    // Request의 Header에서 token 값을 가져온다. "X-AUTH-TOKEN" : "TOKEN값'
    public String resolveToken(HttpServletRequest request) {
        return request.getHeader("X-AUTH-TOKEN");
    }

    public String resolveRefreshToken(HttpServletRequest request) {
        return request.getHeader("X-AUTH-REFRESHTOKEN");
    }

    // 토큰의 유효성 + 만료일자 확인
    public boolean validateToken(String token) {
        try {
            Jws<Claims> claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);
            ValueOperations<String, String> logoutValueOperations = redisTemplate.opsForValue();
            if (logoutValueOperations.get(token) != null) {  // get은 키를 가져온다.
                log.info("로그아웃 된 토큰 입니다.");
                return false;
            }
            // TODO 더 나은 로직을 가져가기
            //            boolean isNotExpire = claims.getBody().getExpiration().after(new Date());
            //            if(null != redisTemplate.opsForValue().get(Constant.REDIS_PREFIX + jwtToken)){
            //                log.info("이미 로그아웃한 사용자");
            //                return false;
            //            }
            return !claims.getBody().getExpiration().before(new Date());
        } catch (Exception e) {
            return false;
        }
    }
}