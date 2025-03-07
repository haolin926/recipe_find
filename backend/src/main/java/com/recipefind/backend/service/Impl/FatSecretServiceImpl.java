package com.recipefind.backend.service.Impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.recipefind.backend.service.FatSecretService;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import org.springframework.http.*;

@Service
public class FatSecretServiceImpl implements FatSecretService {
    private static final String CLIENT_ID = "b7567860082c45d0ac8f4cea5f5641e6 ";
    private static final String CLIENT_SECRET = "05652140f48d484b97a3e2fc92a220e7";
    private static final String TOKEN_URL = "https://oauth.fatsecret.com/connect/token";
    private static final String API_URL = "https://platform.fatsecret.com/rest/server.api";

    private final RestTemplate restTemplate = new RestTemplate();

    private final ObjectMapper objectMapper = new ObjectMapper();
    private String accessToken = null;

    private String getAccessToken() {
        if (accessToken != null) return accessToken;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        String body = "grant_type=client_credentials&client_id=" + CLIENT_ID + "&client_secret=" + CLIENT_SECRET;

        HttpEntity<String> request = new HttpEntity<>(body, headers);

        ResponseEntity<String> response = restTemplate.exchange(TOKEN_URL, HttpMethod.POST, request, String.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            try {
                JsonNode jsonNode = new ObjectMapper().readTree(response.getBody());
                accessToken = jsonNode.get("access_token").asText();
                return accessToken;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }
    @Override
    public String searchRecipes(String keyword) throws JsonProcessingException {
        return null;
    }
}
