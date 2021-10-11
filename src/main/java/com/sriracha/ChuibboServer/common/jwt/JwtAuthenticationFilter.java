/**
 * JwtAuthenticationFilter
 * JWT 토큰을 검증
 *
 * @author jy
 * @version 1.0
 * @see None
 */
package com.sriracha.ChuibboServer.common.jwt;

import com.sriracha.ChuibboServer.common.utils.RedisUtil;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends GenericFilterBean {

    public final static long ACCESS_TOKEN_VALIDATION_SECOND = 1000L * 60 * 10;
    private final JwtTokenProvider jwtTokenProvider;
    private JwtUserDetailService jwtUserDetailService;

    @Autowired
    private RedisUtil redisUtil;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        String refreshJwt = null;
        String refreshUserName = null;

        // 헤더에서 JWT를 받아옴
        String token = jwtTokenProvider.resolveToken((HttpServletRequest) request);

        try {
            // 유효한 토큰인지 확인
            log.info(token);
            if (token != null && jwtTokenProvider.validateToken(token)) {
                // 토큰이 유효하면 토큰으로부터 유저 정보를 받아옴
                Authentication authentication = jwtTokenProvider.getAuthentication(token);
                // SecurityContext 에 Authentication 객체를 저장
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (ExpiredJwtException e) {
            // TODO refreshToken 가져오기
            String refreshToken = jwtTokenProvider.resolveRefreshToken((HttpServletRequest) request);
            log.info(refreshToken);
            // TODO accessToken 만료 신호 && refreshToken 요청
            if (refreshToken != null && jwtTokenProvider.validateToken(token)) {
                refreshJwt = refreshToken;
            }
        } catch (Exception e) {

        }

//        try {
//            if (refreshJwt != null) {
//                refreshUserName = redisUtil.getData(refreshJwt);
//
//                // TODO user이름 매치 시키기
//                if (refreshUserName.equals(jwtTokenProvider.getAuthentication(refreshJwt).getName())) { // true, false를 리턴한다.
//
//                    // TODO 정보 확인하고 정보 불러오기
//                    UserDetails userDetails = jwtUserDetailService.loadUserByUsername(refreshUserName);
//                    UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
//                    usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails((HttpServletRequest) request));
//                    SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
//
//                    // TODO 새로운 access Token 생성
//                    // TODO User 정보를 활용하여 새로운 accessToken 만들
//                    String newAccessToken = jwtTokenProvider
//                            .generateToken("access_token",
//                                    1L, Role.USER, "test@gmail.com", ACCESS_TOKEN_VALIDATION_SECOND);
//
//
//                    // TODO 새로운 accessToken 발급
////                    Cookie newAccessToken = cookieUtil.createCookie(JwtUtil.ACCESS_TOKEN_NAME, newToken);
////                    httpServletResponse.addCookie(newAccessToken);
//                }
//            }
//        } catch (ExpiredJwtException e) {
//            // TODO refreshToken expires시 FE에 failure 메세지 보내기
//            log.info("expired token");
//            // TODO refreshToken의 기간이 일주일 이하 남았다면
//
//            // TODO refeshToken을 다시 갱신할 수 있도록 요청(FE) + 요청 받아 갱신하기(BE)
//        }
        chain.doFilter(request, response);
    }
}