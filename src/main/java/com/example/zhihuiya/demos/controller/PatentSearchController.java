package com.example.zhihuiya.demos.web;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import com.alibaba.fastjson.JSONObject;
import com.example.zhihuiya.model.dto.PatentJson;
import javax.annotation.Resource;

@RestController
@Slf4j
@RequestMapping("/api/patent")
public class PatentSearchController {

    @Resource
    private RestTemplate restTemplate;
    @PostMapping("/search")
    public ResponseEntity<?> searchPatents(@RequestHeader("apikey") String apikey, @RequestBody PatentJson patentJson) {
        // 外部API的URL
        String url = "https://connect.zhihuiya.com/search/patent/query-search-patent?apikey=" + apikey;

        // 设置请求头，使用JSON格式
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer 85abeae1a334416985fbbd18eb7e4e92_e0taz8dgc4"); // 添加 Authorization 头

        // 将 PatentJson 对象转换为 JSON 字符串
        String requestBody = JSONObject.toJSONString(patentJson);
        System.out.printf(requestBody);

        // 构建请求体
        HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);

        try {
            // 发送POST请求
            ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);
            // 返回外部API的响应体
            return ResponseEntity.ok(response.getBody());
        } catch (Exception e) {
            // 如果请求失败，返回错误信息
            return ResponseEntity.status(500).body("请求失败: " + e.getMessage());
        }
    }

}
