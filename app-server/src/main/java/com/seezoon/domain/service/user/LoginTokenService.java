package com.seezoon.domain.service.user;

import com.seezoon.domain.dao.mapper.UserInfoMapper;
import com.seezoon.domain.dao.mapper.UserRefreshTokenMapper;
import com.seezoon.domain.dao.po.UserInfoPO;
import com.seezoon.domain.dao.po.UserRefreshTokenPO;
import com.seezoon.domain.service.user.vo.LoginTokenVO;
import com.seezoon.infrastructure.error.ErrorCode;
import com.seezoon.infrastructure.exception.Assertion;
import com.seezoon.infrastructure.exception.ExceptionFactory;
import com.seezoon.infrastructure.properties.AppProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import javax.crypto.SecretKey;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.HmacAlgorithms;
import org.apache.commons.codec.digest.HmacUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

/**
 * 登录token服务
 */
@Slf4j
@Service
@Validated
public class LoginTokenService {

    /**
     * 新旧refresh token 可以并行5分钟
     */
    public static final int REFRESH_TOKEN_GRACE_PERIOD_SECONDS = 5 * 60;
    public static final int MAX_TOKEN_GENERATION = 5;
    /**
     * 防刷
     */
    public static final int MAX_REFRESH_TOKEN_COUNT = 1000;
    /**
     * 预留按终端管理登录态
     */
    private final static String DEFAULT_CLIENT_ID = "default";
    private final static String USER_SIGN = "user_sign";

    private final SecretKey signKey;
    private final JwtParser jwtParser;
    private final AppProperties appProperties;
    private final UserRefreshTokenMapper userRefreshTokenMapper;
    private final UserInfoMapper userInfoMapper;

    public LoginTokenService(AppProperties appProperties, UserRefreshTokenMapper userRefreshTokenMapper,
            UserInfoMapper userInfoMapper) {
        this.appProperties = appProperties;
        this.userRefreshTokenMapper = userRefreshTokenMapper;
        this.userInfoMapper = userInfoMapper;
        String secretKey = appProperties.getSecretKey();
        this.signKey = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
        this.jwtParser = Jwts.parser().verifyWith(signKey).build();
    }

    /**
     * 创建登录态
     *
     * @param uid
     * @param userSecretKey
     * @return
     */
    @Transactional
    public LoginTokenVO createToken(@NotNull Long uid, @NotEmpty String userSecretKey) {
        List<UserRefreshTokenPO> refreshTokenPOS = userRefreshTokenMapper.selectByUidAndClientIdForUpdate(uid,
                DEFAULT_CLIENT_ID);
        if (refreshTokenPOS.size() >= MAX_REFRESH_TOKEN_COUNT) {
            log.error("login frequent uid:{}, refresh token count:{}", uid, refreshTokenPOS.size());
            throw ExceptionFactory.bizException(ErrorCode.FREQUENT_LOGIN);
        }
        LocalDateTime now = LocalDateTime.now();
        Duration refreshTokenExpire = appProperties.getLogin().getRefreshTokenExpire();
        String refreshTokenId = this.createTokenId();
        String refreshToken = this.createRefreshToken(refreshTokenId, uid, userSecretKey, refreshTokenExpire);

        UserRefreshTokenPO refreshTokenPO = new UserRefreshTokenPO();
        refreshTokenPO.setUid(uid);
        refreshTokenPO.setClientId(DEFAULT_CLIENT_ID);
        refreshTokenPO.setRefreshTokenId(refreshTokenId);
        refreshTokenPO.setTokenGeneration(1);
        refreshTokenPO.setStatus(UserRefreshTokenPO.VALID);
        refreshTokenPO.setIssueTime(now);
        refreshTokenPO.setExpireTime(now.plus(refreshTokenExpire));
        refreshTokenPO.setUpdateTime(now);
        int affectedRow = userRefreshTokenMapper.insert(refreshTokenPO);
        Assertion.affectedOne(affectedRow);

        // 将其他有效的refreshToken，置换为替换
        refreshTokenPOS.stream().filter(v -> v.getStatus() == UserRefreshTokenPO.VALID).forEach(v -> {
            v.setReplacedTime(now);
            v.setGracePeriodEnd(now.plusSeconds(REFRESH_TOKEN_GRACE_PERIOD_SECONDS));
            v.setStatus(UserRefreshTokenPO.REPLACED);
            v.setUpdateTime(now);
            Assertion.affectedOne(this.userRefreshTokenMapper.updateByPrimaryKey(v));
        });

        Duration accessTokenExpire = appProperties.getLogin().getAccessTokenExpire();
        String accessTokenId = this.createTokenId();
        String accessToken = this.createAccessToken(accessTokenId, uid, accessTokenExpire);

        LoginTokenVO loginTokenVO = new LoginTokenVO();
        loginTokenVO.setUid(uid);
        loginTokenVO.setAccessToken(accessToken);
        loginTokenVO.setAccessTokenExpire(accessTokenExpire.getSeconds());
        loginTokenVO.setRefreshToken(refreshToken);
        loginTokenVO.setRefreshTokenExpire(refreshTokenExpire.getSeconds());
        return loginTokenVO;
    }

    /**
     * 刷新refresh token
     *
     * @param refreshToken
     * @return not null
     */
    @Transactional
    public LoginTokenVO refreshToken(@NotEmpty String refreshToken) {
        // 过期、格式错误、签名错误都是有异常 JwtException
        Jws<Claims> claimsJws = null;
        try {
            claimsJws = jwtParser.parseSignedClaims(refreshToken);
        } catch (JwtException e) {
            log.error("jwt access token parse error {}", e.getMessage());
            throw ExceptionFactory.bizException(ErrorCode.REFRESH_TOKEN_ERROR);
        }
        Claims payload = claimsJws.getPayload();
        // 验证jid 是否有效
        String jid = payload.getId();
        Long uid = Long.valueOf(payload.getSubject());

        UserRefreshTokenPO refreshTokenPO = userRefreshTokenMapper.selectByRefreshTokenId(jid);
        if (refreshTokenPO == null) {
            log.error("uid:{} refresh token id not found {}", uid, jid);
            throw ExceptionFactory.bizException(ErrorCode.REFRESH_TOKEN_ERROR);
        }
        if (!Objects.equals(refreshTokenPO.getUid(), uid)) {
            log.error("uid:{} not match refresh token {} actual uid", uid, jid, refreshTokenPO.getUid());
            throw ExceptionFactory.bizException(ErrorCode.REFRESH_TOKEN_ERROR);
        }
        // 避免用一个refresh token 一直刷
        if (refreshTokenPO.getTokenGeneration() > MAX_TOKEN_GENERATION) {
            log.error("uid:{} token generation exceed max_token_generation[] current:{}", uid, MAX_TOKEN_GENERATION,
                    refreshTokenPO.getTokenGeneration());
            throw ExceptionFactory.bizException(ErrorCode.REFRESH_TOKEN_ERROR);
        }
        LocalDateTime now = LocalDateTime.now();
        // 刷新token过期
        if (refreshTokenPO.getExpireTime().isBefore(now)) {
            log.error("uid refresh token expired at {}", refreshTokenPO.getExpireTime());
            throw ExceptionFactory.bizException(ErrorCode.REFRESH_TOKEN_ERROR);
        }
        // 过期或者不在过渡窗口
        if (refreshTokenPO.getStatus() == UserRefreshTokenPO.REPLACED && refreshTokenPO.getGracePeriodEnd()
                .isBefore(now)) {
            log.error("uid:{} refresh token replaced at {} GracePeriodEnd {}", uid, refreshTokenPO.getReplacedTime(),
                    refreshTokenPO.getGracePeriodEnd());
            throw ExceptionFactory.bizException(ErrorCode.REFRESH_TOKEN_ERROR);
        }
        // token 代数
        refreshTokenPO.setTokenGeneration(refreshTokenPO.getTokenGeneration() + 1);
        Assertion.affectedOne(this.userRefreshTokenMapper.updateByPrimaryKey(refreshTokenPO));

        UserInfoPO userInfoPO = this.userInfoMapper.selectByPrimaryKey(uid);
        if (userInfoPO == null) {
            throw ExceptionFactory.bizException(ErrorCode.USER_NOT_EXISTS);
        }

        String hmac = payload.get(USER_SIGN, String.class);

        String expectHmac = new HmacUtils(HmacAlgorithms.HMAC_SHA_256,
                userInfoPO.getSecretKey().getBytes(StandardCharsets.UTF_8)).hmacHex(String.valueOf(uid));
        if (!expectHmac.equals(hmac)) {
            log.error("uid:{} refreshToken hamc not match actual:{},expected", uid, hmac, expectHmac);
            throw ExceptionFactory.bizException(ErrorCode.REFRESH_TOKEN_ERROR);
        }

        return this.createToken(uid, userInfoPO.getSecretKey());
    }

    @Transactional
    public int logout(@NotNull Long uid) {
        return this.userRefreshTokenMapper.deleteByUidAndClientId(uid, DEFAULT_CLIENT_ID);
    }

    @Transactional
    public int clear() {
        return userRefreshTokenMapper.deleteByExpireToken(LocalDateTime.now());
    }

    /**
     * 创建access token
     *
     * @param id
     * @param uid
     * @param expire
     * @return
     */
    private String createAccessToken(@NotEmpty String id, @NotNull Long uid, @NotNull Duration expire) {
        String compacted = Jwts.builder()
                .id(id)
                .setSubject(String.valueOf(uid)).issuedAt(Date.from(Instant.now()))
                .expiration(Date.from(Instant.now().plusSeconds(expire.getSeconds())))
                .signWith(signKey).compact();
        return compacted;
    }

    /**
     * 创建refresh token
     *
     * @param id
     * @param uid
     * @param userSecretKey
     * @param expire
     * @return
     */
    private String createRefreshToken(@NotEmpty String id, @NotNull Long uid, @NotEmpty String userSecretKey,
            @NotNull Duration expire) {
        Instant now = Instant.now();
        String compacted = Jwts.builder()
                .id(id)
                .subject(String.valueOf(uid))
                .claim(USER_SIGN, new HmacUtils(HmacAlgorithms.HMAC_SHA_256,
                        userSecretKey.getBytes(StandardCharsets.UTF_8)).hmacHex(String.valueOf(uid)))
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusSeconds(expire.getSeconds())))
                .signWith(signKey).compact();
        return compacted;
    }


    private String createTokenId() {
        return UUID.randomUUID().toString();
    }
}
