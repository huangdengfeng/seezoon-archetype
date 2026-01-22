package com.seezoon.interfaces;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.seezoon.BaseApplicationTest;
import com.seezoon.application.user.dto.RefreshTokenCmd;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

@Disabled
class LoginControllerTest extends BaseApplicationTest {


    @Test
    void userPwdLogin() throws Exception {
        // password 字段有 @JsonProperty(access = WRITE_ONLY)，需要直接构造 JSON
        String json = """
                {
                    "username": "admin",
                    "password": "123456"
                }
                """;

        mockMvc.perform(post("/login/user_pwd")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").isNotEmpty());
    }

    @Test
    void refreshToken() throws Exception {
        RefreshTokenCmd cmd = new RefreshTokenCmd();
        cmd.setRefreshToken("test-refresh-token");

        mockMvc.perform(post("/login/refresh_token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(cmd)))
                .andDo(print());
    }
}