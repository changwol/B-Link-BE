package com.blink.server.configure;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        String frontServerUrl = "http://localhost:3000";
        registry.addMapping("/**") // 모든 엔드포인트에 대해 CORS 허용
                .allowedOrigins(frontServerUrl) // React 애플리케이션의 URL을 허용
                .allowedMethods("GET", "POST", "PUT", "DELETE") // 허용할 HTTP 메서드
                .allowedHeaders("*"); // 모든 헤더 허용
    }
}//첫 대화 가져오기
