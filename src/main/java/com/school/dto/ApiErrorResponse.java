package com.school.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ApiErrorResponse {
    private boolean success = false;
    private String message;
    private LocalDateTime timestamp;
    private int status;
    private Map<String, String> fieldErrors;
    private List<String> errors;
}
