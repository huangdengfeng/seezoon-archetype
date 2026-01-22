package com.seezoon.infrastructure.rpc.wx;

import com.seezoon.BaseApplicationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class WxOauthAccessTokenServiceTest extends BaseApplicationTest {

    @Autowired
    private WxOauthAccessTokenService wxOauthAccessTokenService;

    @Test
    void execute() {
        wxOauthAccessTokenService.execute("appId", "secret", "code");
    }
}