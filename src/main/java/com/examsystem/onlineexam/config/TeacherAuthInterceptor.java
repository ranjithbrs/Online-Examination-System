package com.examsystem.onlineexam.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class TeacherAuthInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HttpSession session = request.getSession(false);
        boolean isTeacher = session != null && Boolean.TRUE.equals(session.getAttribute("teacherLoggedIn"));

        if (!isTeacher) {
            response.sendRedirect("/teacher/login?unauthorized=true");
            return false;
        }
        return true;
    }
}
