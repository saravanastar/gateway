package com.jbhunt.fms.gateway.filter;

import com.jbhunt.fms.gateway.dto.CustomerCredentials;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

@Component
@Slf4j
public class ClientAuthFilter extends OncePerRequestFilter {

    @Autowired
    RestTemplate restTemplate;

    static String TOKEN_PREFIX = "Bearer ";
    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {
        CustomerCredentials userName = null;
        String headerValue = httpServletRequest.getHeader("Authorization");
        if (headerValue != null) {
            if (headerValue.startsWith(TOKEN_PREFIX)) {
                String token = headerValue.replace(TOKEN_PREFIX, "");
                CustomerCredentials customerCredentials = CustomerCredentials.builder().token(token).build();

                HttpEntity<CustomerCredentials> entity = new HttpEntity<CustomerCredentials>(customerCredentials);

                ResponseEntity<CustomerCredentials> responseEntity = restTemplate.exchange("http://localhost:8081/auth/validate", HttpMethod.POST,entity, CustomerCredentials.class);
                if (!responseEntity.getStatusCode().is2xxSuccessful()) {
                    log.error("Not a valid token or user");
                } else {
                    userName = responseEntity.getBody();
                }
            }
        }

        if (userName != null &&
        SecurityContextHolder.getContext().getAuthentication() == null) {
            ResponseEntity<CustomerCredentials> responseEntity = restTemplate.getForEntity("http://localhost:8081/auth/cusomter/" + userName.getClientId(), CustomerCredentials.class);
            CustomerCredentials customerCredentials = responseEntity.getBody();

            UsernamePasswordAuthenticationToken authentication = new
                    UsernamePasswordAuthenticationToken(customerCredentials, null, null);

            authentication.setDetails(new
                    WebAuthenticationDetailsSource().buildDetails(httpServletRequest));

            SecurityContextHolder.getContext().setAuthentication(
                    authentication);

        }
        filterChain.doFilter(httpServletRequest, httpServletResponse);
    }
}
