package com.seezoon.infrastructure.configuration.interceptor;

import com.seezoon.infrastructure.configuration.context.SecurityContext;
import com.seezoon.infrastructure.configuration.context.SecurityContextHolder;
import com.seezoon.infrastructure.exception.Assertion;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.nio.charset.StandardCharsets;
import javax.crypto.SecretKey;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.HandlerInterceptor;

@Slf4j
public class SessionInterceptor implements HandlerInterceptor {

    private static final String PREFIX = "Bearer ";
    private static int PREFIX_LENGTH = PREFIX.length();
    private final JwtParser jwtParser;

    public SessionInterceptor(String secretKey) {
        Assertion.notEmpty(secretKey, "secretKey is empty");
        SecretKey signKey = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
        this.jwtParser = Jwts.parser().verifyWith(signKey).build();
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (StringUtils.isEmpty(authorization) || !authorization.startsWith(PREFIX)
                || authorization.length() <= PREFIX_LENGTH) {
            // 401 未登录
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            return false;
        }

        String accessToken = authorization.substring(PREFIX_LENGTH);

        // 过期、格式错误、签名错误都是有异常 JwtException
        Jws<Claims> claimsJws = null;
        try {
            claimsJws = jwtParser.parseSignedClaims(accessToken);
        } catch (JwtException e) {
            log.error("jwt access token parse error {}", e.getMessage());
            // 401 未登录
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            return false;
        }
        Claims payload = claimsJws.getPayload();

        if (StringUtils.isEmpty(payload.getSubject())) {
            // 401 未登录
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            return false;
        }
        Long uid = Long.valueOf(payload.getSubject());
        // 放入uid 到上下文
        SecurityContextHolder.set(new SecurityContext(uid));
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
            throws Exception {
        SecurityContextHolder.clear();
    }

}
