package com.example.zhihuiya.demos.controller;

import com.example.zhihuiya.exception.UnauthorizedException;
import com.example.zhihuiya.model.dto.PatentJson;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

@RestController
@Slf4j
@RequestMapping("/api/patent")
public class PatentSearchController {

    @Resource
    private RestTemplate restTemplate;

    @Resource
    private ObjectMapper objectMapper;  // 注入 Jackson 的 ObjectMapper 解析json数据

    /**
     * 通过传入的 apikey 和 token 去请求外部 API
     * @param apikey 路径参数中的 apikey
     * @param token 路径参数中的 token
     * @param patentJson 专利搜索的参数
     * @return ResponseEntity
     */
    @PostMapping("/search/{apikey}/{token}")
    public ResponseEntity<?> searchPatents(
            @PathVariable String apikey,
            @PathVariable String token,
            @RequestBody PatentJson patentJson) {

        // 会话未登录或者缓存过期都会抛出 UnauthorizedException 异常
        if (apikey == null || token == null) {
            throw new UnauthorizedException("token已过期或未存在，请重新登录");
        }

        // 外部API的URL
        String url = "https://connect.zhihuiya.com/search/patent/query-search-patent?apikey=" + apikey;

        // 设置请求头，使用JSON格式
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + token); // 从 session 中取出 token

        String requestBody;
        try {
            // 使用 Jackson 将 PatentJson 对象转换为 JSON 字符串
            requestBody = objectMapper.writeValueAsString(patentJson);
        } catch (JsonProcessingException e) {
            // 如果 JSON 转换失败，返回错误信息
            return ResponseEntity.status(400).body("JSON 格式错误: " + e.getMessage());
        }

        // 构建请求体
        HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);

        try {
            // 发送POST请求
            ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);

            return ResponseEntity.ok(response.getBody());
        } catch (Exception e) {

            return ResponseEntity.status(500).body("请求失败: " + e.getMessage());
        }
    }
}
