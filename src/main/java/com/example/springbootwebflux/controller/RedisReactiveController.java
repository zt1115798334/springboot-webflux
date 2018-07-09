package com.example.springbootwebflux.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.springbootwebflux.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.ReactiveValueOperations;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("redis")
public class RedisReactiveController {

    @Autowired
    private ReactiveRedisTemplate reactiveRedisTemplate;

    @PostMapping("save")
    public Mono saveUser(@RequestBody User user) {
        String key = "user_" + user.getId();
        ReactiveValueOperations ops = reactiveRedisTemplate.opsForValue();
        return ops.getAndSet(key, user);
    }

    @GetMapping("find")
    public Mono findById(@RequestParam String id) {
        String key = "user_" + id;
        ReactiveValueOperations ops = reactiveRedisTemplate.opsForValue();
        return ops.get(key);
    }

    @DeleteMapping("delete")
    public Mono deleteById(@RequestParam String id) {
        String key = "user_" + id;
        ReactiveValueOperations ops = reactiveRedisTemplate.opsForValue();
        return ops.delete(key);
    }

    public static void main(String[] args) {
        User user = new User();
        user.setId("1");
        user.setUsername("zhangtong");
        user.setPhone("123456");
        user.setEmail("123@qq.com");
        JSONObject jsonObject = JSON.parseObject(JSON.toJSONString(user));
        System.out.println(jsonObject.toJSONString());
    }
}
