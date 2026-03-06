package com.seezoon.domain.service.sys.authentication;

import com.seezoon.domain.service.sys.valueobj.UserVO;
import com.seezoon.infrastructure.exception.Assertion;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * 用户登录服务
 */
@Slf4j
@RequiredArgsConstructor
@Service
@Validated
public class SessionService {

    private static final String USER_ATTRIBUTE = "user";

    public UserVO getSessionData() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            return null;
        }
        HttpServletRequest request = attributes.getRequest();
        HttpSession session = request.getSession(false);
        if (null == session) {
            return null;
        }
        UserVO userVO = (UserVO) session.getAttribute(USER_ATTRIBUTE);
        return userVO;
    }

    /**
     * 创建会话
     *
     * @param userVO 用户信息
     * @return 会话ID
     */
    public String createSession(@NotNull UserVO userVO) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        Assertion.notNull(attributes, "servlet request attributes is null");
        HttpServletRequest request = attributes.getRequest();
        HttpSession session = request.getSession(true);
        session.setAttribute(USER_ATTRIBUTE, userVO);
        String sessionId = session.getId();
        log.info("create session success, sessionId:{}, uid:{}", sessionId, userVO.getUid());
        return sessionId;
    }

    public void invalid() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        Assertion.notNull(attributes, "servlet request attributes is null");
        HttpServletRequest request = attributes.getRequest();
        HttpSession session = request.getSession(false);
        if (null == session) {
            return;
        }
        session.invalidate();
    }
}
