package com.blink.server.configure;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    private static final Logger logger = LoggerFactory.getLogger(WebMvcConfig.class);
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        String frontServerUrl = "http://localhost:3000";
        String Url2 = "http://127.0.0.1:3000";
        registry.addMapping("/*") // 모든 엔드포인트에 대해 CORS 허용
                .allowedOrigins(frontServerUrl,Url2) // React 애플리케이션의 URL을 허용
                .allowedMethods("GET", "POST", "PUT", "DELETE") // 허용할 HTTP 메서드
                .allowedHeaders("*"); // 모든 헤더 허용
        logger.info("CORS 설정 적용: {} 및 {}", frontServerUrl, Url2);
    }
}
