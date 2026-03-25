package com.seezoon.infrastructure.rpc.wx;

import com.seezoon.infrastructure.error.ErrorCode;
import com.seezoon.infrastructure.exception.ExceptionFactory;
import com.seezoon.infrastructure.rpc.wx.dto.WxGetPhoneNumberReq;
import com.seezoon.infrastructure.rpc.wx.dto.WxGetPhoneNumberResp;
import jakarta.validation.constraints.NotEmpty;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * 获取小程序用户手机号
 * <p>
 * 将小程序端 getPhoneNumber 获取的 code 换取用户手机号。每个 code 只能使用一次，有效期 5 分钟。
 * 接口需在服务端调用，不可在前端直接调用。
 *
 * @see <a
 *         href="https://developers.weixin.qq.com/miniprogram/dev/server/API/user-info/phone-number/api_getphonenumber.html">获取手机号</a>
 */
@RequiredArgsConstructor
@Slf4j
@Component
@Validated
public class WxGetPhoneNumberService {

    private static final String API_URL = "https://api.weixin.qq.com/wxa/business/getuserphonenumber";

    private final RestClient restClient;

    /**
     * 通过 code 换取用户手机号
     *
     * @param accessToken 接口调用凭证，可使用 getStableAccessToken 获取
     * @param code 手机号获取凭证（小程序端 button open-type="getPhoneNumber" 获取的 code）
     * @return 用户手机号信息，含 phoneNumber、purePhoneNumber、countryCode
     */
    public WxGetPhoneNumberResp execute(@NotEmpty String accessToken, @NotEmpty String code) {
        URI uri = UriComponentsBuilder.fromUriString(API_URL)
                .queryParam("access_token", accessToken)
                .build()
                .toUri();
        WxGetPhoneNumberReq req = new WxGetPhoneNumberReq();
        req.setCode(code);
        if (log.isDebugEnabled()) {
            log.debug("call getuserphonenumber code={}", code);
        }
        try {
            WxGetPhoneNumberResp resp = restClient.post()
                    .uri(uri)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(req)
                    .retrieve()
                    .body(WxGetPhoneNumberResp.class);
            if (!resp.success()) {
                log.error("call wx getuserphonenumber error code:{},msg:{}", resp.getErrcode(), resp.getErrmsg());
                throw ExceptionFactory.bizException(ErrorCode.WX_ERROR);
            }
            if (resp.getPhoneInfo() == null || StringUtils.isEmpty(resp.getPhoneInfo().getPhoneNumber())) {
                log.error("call wx getuserphonenumber empty phone");
                throw ExceptionFactory.bizException(ErrorCode.WX_ERROR);
            }
            log.info("call getuserphonenumber success:{}", resp != null && resp.success());
            return resp;
        } catch (Exception e) {
            log.error("call wx getuserphonenumber error", e);
            throw ExceptionFactory.bizException(ErrorCode.WX_ERROR);
        }
    }
}
