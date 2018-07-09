package com.example.springbootwebflux;

import com.example.springbootwebflux.entity.MyEvent;
import com.example.springbootwebflux.entity.User;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.time.Duration;

public class MyTest {

    private WebClient webClient = null;

    @Before
    public void before() {
        webClient = WebClient.builder().baseUrl("http://127.0.0.1:8081").build();
    }

    @Test
    public void webClientTests() {
        //使用WebClientBuilder来构建WebClient对象；
        webClient.get().uri("/user")
                .accept(MediaType.APPLICATION_STREAM_JSON)  //配置请求Header：Content-Type: application/stream+json；
                .exchange() //获取response信息，返回值为ClientResponse，retrive()可以看做是exchange()方法的“快捷版”；
                .flatMapMany(clientResponse -> clientResponse.bodyToFlux(User.class))   //使用flatMap来将ClientResponse映射为Flux；
                .doOnNext(System.out::println)  //只读地peek每个元素，然后打印出来，它并不是subscribe，所以不会触发流；
                .blockLast();   //blockLast方法，顾名思义，在收到最后一个元素前会阻塞，响应式业务场景中慎用
    }

    @Test
    public void webClientTests3() {
        webClient.get().uri("times")
                .accept(MediaType.TEXT_EVENT_STREAM)
                .retrieve()
                .bodyToFlux(String.class)
                .log()
                .take(10)
                .blockLast();
    }

    @Test
    public void webClientTests4() {
        Flux<MyEvent> eventFlux = Flux.interval(Duration.ofSeconds(1))
                .map(l -> new MyEvent(System.currentTimeMillis(), "message-" + l)).take(5); // 1
        webClient
                .post().uri("events")
                .contentType(MediaType.APPLICATION_STREAM_JSON) // 2
                .body(eventFlux, MyEvent.class) // 3
                .retrieve()
                .bodyToMono(Void.class)
                .block();
    }

}
