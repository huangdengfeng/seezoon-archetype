package com.seezoon.interfaces;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.seezoon.BaseApplicationTest;
import com.seezoon.application.user.dto.ChangePasswordCmd;
import com.seezoon.application.user.dto.UpdateUserProfileCmd;
import java.time.LocalDate;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

@Disabled
class UserControllerTest extends BaseApplicationTest {


    @Test
    void info() throws Exception {
        mockMvc.perform(get("/user/info")
                        .header("uid", "1000000"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.uid").isNumber());
    }

    @Test
    void updateProfile() throws Exception {
        UpdateUserProfileCmd cmd = new UpdateUserProfileCmd();
        cmd.setNickName("测试昵称");
        cmd.setName("测试姓名");
        cmd.setEmail("test@example.com");
        cmd.setBirthday(LocalDate.of(1990, 1, 1));
        cmd.setAddress("测试地址");

        mockMvc.perform(post("/user/update")
                        .header("Authorization", "Bearer "
                                + "eyJhbGciOiJIUzI1NiJ9.eyJqdGkiOiJiYWFiMmViNC1kNjk5LTRjZGQtODdkNi1hZDRhNTk0MWIzOWQiLCJzdWIiOiIxIiwiaWF0IjoxNzY3MDY4MzQwLCJleHAiOjE3NjcwNzU1NDB9.ybqKHJUR-k6zA4DUy2ARshNmKw5YzSHG0AT5-59rysY")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(cmd)))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    void logout() throws Exception {
        mockMvc.perform(post("/user/logout")
                        .header("uid", "1000000"))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    void changePassword() throws Exception {
        ChangePasswordCmd cmd = new ChangePasswordCmd();
        cmd.setOldPassword("oldPassword123");
        cmd.setNewPassword("newPassword123");

        mockMvc.perform(post("/user/change_password")
                        .header("uid", "1000000")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(cmd)))
                .andDo(print())
                .andExpect(status().isOk());
    }
}