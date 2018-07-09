package com.example.springbootwebflux.controller;

import com.example.springbootwebflux.entity.User;
import com.example.springbootwebflux.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Controller
@RequestMapping("user")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("")
    public Mono<User> save(User user) {
        return this.userService.save(user);
    }

    @DeleteMapping("/{username}")
    public Mono<Long> deleteByUsername(@PathVariable String username) {
        return this.userService.deleteByUsername(username);
    }

    @GetMapping("/{username}")
    public Mono<User> findByUsername(@PathVariable String username) {
        return this.userService.findByUsername(username);
    }

    @GetMapping(value = "",produces =MediaType.APPLICATION_STREAM_JSON_VALUE)
    public Flux<User> findAll() {
        return this.userService.findAll().delayElements(Duration.ofSeconds(1));
    }

    @GetMapping("helloPage")
    public Mono<String> helloPage(final Model model){
        model.addAttribute("city","北京");
        model.addAttribute("name","xiaomi");

        Flux<User> all = userService.findAll();
        model.addAttribute("userList",all);
        return Mono.create(stringMonoSink -> stringMonoSink.success("hello"));
    }
}
