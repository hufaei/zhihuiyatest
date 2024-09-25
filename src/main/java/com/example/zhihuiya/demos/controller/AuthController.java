package com.example.zhihuiya.demos.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import javax.servlet.http.HttpSession;
import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Resource
    private RestTemplate restTemplate;

    /**
     * 通过不暴露的clientId和clientSecret的输入获取token
     * @param request 包含clientId和clientSecret的json请求体
     * @return ResponseEntity
     */
    @PostMapping("/token")
    public ResponseEntity<?> generateToken(@RequestBody Map<String, String> request) {
        // 获取clientId和clientSecret从JSON请求体中
        String clientId = request.get("clientId");
        String clientSecret = request.get("clientSecret");

        // 校验 clientId 和 clientSecret 是否有效
        if (clientId == null || clientId.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("clientId 不能为空");
        }
        if (clientSecret == null || clientSecret.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("clientSecret 不能为空");
        }
        // token连接url
        String url = "https://connect.zhihuiya.com/oauth/token";

        // 创建连接请求对象
        String auth = clientId + ":" + clientSecret;
        String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes(StandardCharsets.UTF_8));
        String authHeader = "Basic " + encodedAuth;

        // 请求对象写入请求头部
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.set("Authorization", authHeader);

        // 请求体添加必要参数
        MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
        requestBody.add("grant_type", "client_credentials");

        // 初始化响应实体
        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(requestBody, headers);

        try {
            // post请求获取响应体数据
            ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);

            // 解析响应，提取所需token
            String responseBody = response.getBody();
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(responseBody);
            String token = jsonNode.path("data").path("token").asText();

            return ResponseEntity.ok(responseBody);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("请求失败: " + e.getMessage());
        }
    }
}
