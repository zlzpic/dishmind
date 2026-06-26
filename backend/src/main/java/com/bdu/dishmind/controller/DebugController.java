package com.bdu.dishmind.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/debug")
public class DebugController {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @GetMapping("/buffer")
    public Map<String, Object> checkBuffer() {
        Long size = redisTemplate.opsForList().size("behavior:buffer:list");
        List<String> items = redisTemplate.opsForList().range("behavior:buffer:list", 0, 9);

        Map<String, Object> map = new HashMap<>();
        map.put("bufferSize", size);
        map.put("first10Items", items);
        return map;
    }
}
