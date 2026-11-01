package com.examsystem.onlineexam.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    private final TeacherAuthInterceptor teacherAuthInterceptor;

    public WebMvcConfig(TeacherAuthInterceptor teacherAuthInterceptor) {
        this.teacherAuthInterceptor = teacherAuthInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(teacherAuthInterceptor)
                .addPathPatterns("/admin/**", "/history");
    }
}
