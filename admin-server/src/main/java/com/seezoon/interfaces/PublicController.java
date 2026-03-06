package com.seezoon.interfaces;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/public")
public class PublicController {

    @GetMapping("/session")
    public String session() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            throw new RuntimeException("当前无请求上下文（非Web请求/异步线程）");
        }

        HttpServletRequest request = attributes.getRequest();
        // Spring Session 会拦截 getSession() 方法，返回封装后的 Session 实例
        HttpSession session = request.getSession(false); // false：无Session时返回null，避免创建空Session
        if (session == null) {
            return "无有效会话";
        }

        return "hello session";
    }
}
