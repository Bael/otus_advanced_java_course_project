package com.github.bael.otus.java.calendar.interop.user;
// com.github.bael.otus.java.calendar.client.UserServiceClient


import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class UserServiceClient {

    private final RestTemplate restTemplate;
    private final String userServiceUrl;

    public UserServiceClient(RestTemplateBuilder restTemplateBuilder,
                             @Value("${user-service.url:http://localhost:12101}") String userServiceUrl) {
        this.restTemplate = restTemplateBuilder.build();
        this.userServiceUrl = userServiceUrl;
    }

    /**
     * Запрашивает пользователей по списку ID и возвращает Map<UUID, UserAccountResponse>
     */
    public Map<UUID, UserInfo> getUsersByIds(Set<UUID> userIds) {
        if (userIds.isEmpty()) {
            return Map.of();
        }

        // Формируем URI
        String url = userServiceUrl + "/api/v1/users/";

        // Заголовки
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Set<UUID>> request = new HttpEntity<>(userIds, headers);

        try {
            ResponseEntity<UserAccountResponse[]> response = restTemplate
                    .exchange(url, HttpMethod.POST, request, UserAccountResponse[].class);

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                return Arrays.stream(response.getBody())
                        .collect(Collectors.toMap(UserAccountResponse::getId, user -> toUserInfo(user)));
            } else {
                throw new RuntimeException("User service returned non-OK status: " + response.getStatusCode());
            }
        } catch (RestClientException e) {
            throw new RuntimeException("Failed to fetch users from user-service: " + e.getMessage(), e);
        }
    }

    private UserInfo toUserInfo(UserAccountResponse user) {
        return UserInfo.builder()
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .build();
    }
}