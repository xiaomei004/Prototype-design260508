package com.example.campusanimal.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AuditRequest {

    @NotBlank
    private String auditStatus;
}
