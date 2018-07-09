package com.example.springbootwebflux.controller;

import com.example.springbootwebflux.entity.MyEvent;
import com.example.springbootwebflux.repository.MyEventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("events")
public class MyEventController {

    @Autowired
    private MyEventRepository eventRepository;

    @PostMapping(path = "")
    public Mono<Void> LoadEvents(@RequestBody Flux<MyEvent> eventFlux) {
        return this.eventRepository.saveAll(eventFlux).then();
    }

    @GetMapping(params = "", produces = MediaType.APPLICATION_STREAM_JSON_VALUE)
    public Flux<MyEvent> getEvents() {
        System.out.println("MyEventController.getEvents");
        return this.eventRepository.findAll();
    }
}
