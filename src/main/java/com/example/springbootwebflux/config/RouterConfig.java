package com.example.springbootwebflux.config;

import com.example.springbootwebflux.entity.MyEvent;
import com.example.springbootwebflux.handler.CityHandler;
import com.example.springbootwebflux.handler.TimeHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.CollectionOptions;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class RouterConfig {

    @Autowired
    private TimeHandler timeHandler;

    @Autowired
    private CityHandler cityHandler;

    @Bean
    public RouterFunction<ServerResponse> timerRouter() {
        return route(GET("/time"), req -> timeHandler.getTime(req))
                .andRoute(GET("/date"), timeHandler::getDate)
                .andRoute(GET("/times"), timeHandler::sendTimePerSec)
                .andRoute(GET("/cityHello"), cityHandler::helloCity);
    }


    /**
     * 对于复杂的Bean只能通过Java Config的方式配置，这也是为什么Spring3之后官方推荐这种配置方式的原因，这段代码可以放到配置类中，本例我们就直接放到启动类WebFluxDemoApplication了；
     *
     * @param operations MongoOperations提供对MongoDB的操作方法，由Spring注入的mongo实例已经配置好，直接使用即可；
     * @return
     */
    @Bean
    public CommandLineRunner initData(MongoOperations operations) {
        return (String... args) -> {    // CommandLineRunner也是一个函数式接口，其实例可以用lambda表达；
            operations.dropCollection(MyEvent.class);    // 如果有，先删除collection，生产环境慎用这种操作；
            operations.createCollection(MyEvent.class, CollectionOptions.empty().maxDocuments(200).size(100000).capped()); // 创建一个记录个数为10的capped的collection，容量满了之后，新增的记录会覆盖最旧的。
        };
    }

}
