package com.example.zhihuiya.demos.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import javax.servlet.http.HttpSession;
import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Resource
    private RestTemplate restTemplate;

    @PostMapping("/token")
    public ResponseEntity<?> generateToken(@RequestParam String clientId, @RequestParam String clientSecret, HttpSession session) {
        // Base URL format with embedded credentials
        String url = "https://connect.zhihuiya.com/oauth/token";

        // Create Basic Auth header using Base64 encoding
        String auth = clientId + ":" + clientSecret;
        String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes(StandardCharsets.UTF_8));
        String authHeader = "Basic " + encodedAuth;

        // Set headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.set("Authorization", authHeader);  // Add Basic Auth header

        // Manually encode form parameters for the body
        MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
        requestBody.add("grant_type", "client_credentials");

        // Create the request entity with headers and body
        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(requestBody, headers);

        try {
            // Send POST request to fetch the token
            ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);

            // Extract token from the response body (assumes JSON structure)
            // Assuming the token field is located in `data.token`
            String responseBody = response.getBody();
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(responseBody);
            String token = jsonNode.path("data").path("token").asText();

            // Save token and clientId to session
            session.setAttribute("token", token);
            session.setAttribute("apikey", clientId);

            // Return the response body (access token)
            return ResponseEntity.ok(responseBody);
        } catch (Exception e) {
            // Handle and return error message
            return ResponseEntity.status(500).body("请求失败: " + e.getMessage());
        }
    }
}
