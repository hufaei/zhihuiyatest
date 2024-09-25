package com.example.zhihuiya.demos.web;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Resource
    private RestTemplate restTemplate;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestParam String clientId, @RequestParam String clientSecret, @RequestParam String apikey, HttpSession session) {
        // 请求 token 的 URL
        String url = "https://connect.zhihuiya.com/oauth/token";

        // 设置请求头和请求体
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("grant_type", "client_credentials");

        try {
            // 发送请求获取 token
            ResponseEntity<Map> response = restTemplate.postForEntity(url, requestBody, Map.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                Map<String, Object> responseBody = response.getBody();
                String token = (String) responseBody.get("token");

                // 将 apikey 和 token 存入 session
                session.setAttribute("apikey", apikey);
                session.setAttribute("token", token);

                return ResponseEntity.ok("登录成功, Token 已存储.");
            } else {
                return ResponseEntity.status(response.getStatusCode()).body("登录失败");
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).body("请求失败: " + e.getMessage());
        }
    }
}
