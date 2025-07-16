package com.life.bank.palm.service.ai.dto;

import lombok.Data;
import java.util.List;

@Data
public class AiRequest {
    private String model;
    private List<AiMessage> messages;
    private Double temperature;
    private Integer max_tokens;
    private Boolean stream = true;
}