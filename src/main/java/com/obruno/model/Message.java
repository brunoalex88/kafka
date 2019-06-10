package com.obruno.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Message {
    private Long id;
    private String text;
}
