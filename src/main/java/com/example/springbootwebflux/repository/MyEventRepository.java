package com.example.springbootwebflux.repository;

import com.example.springbootwebflux.entity.MyEvent;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface MyEventRepository extends ReactiveCrudRepository<MyEvent, String> {

//    @Tailable
//    Flux<MyEvent> findBy();
}
