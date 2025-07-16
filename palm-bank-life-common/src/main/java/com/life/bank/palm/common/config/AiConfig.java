package com.life.bank.palm.common.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "ai.deepseek")
public class AiConfig {
    private String apiKey;
    private String apiUrl = "https://api.deepseek.com/v1/chat/completions";
    private String model = "deepseek-chat";
    private Double temperature = 0.7;
    private Integer maxTokens = 2048;
}