package com.alexbezsh.microservices.configserver.model.api;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PropertyDto {

    @NotBlank
    private String appName;

    @NotBlank
    private String profile;

    private String label;

    @NotBlank
    private String key;

    @NotBlank
    private String value;

}
