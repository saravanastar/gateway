package com.jbhunt.fms.gateway.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CustomerCredentials {

    private String clientId;
    private String clientSecret;
    private String token;
}
