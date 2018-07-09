package com.example.springbootwebflux;

import org.junit.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

public class ReactorTest {
    @Test
    public void test1() {
        Integer[] array = new Integer[]{1, 2, 3, 4, 5, 6};
        Flux.fromArray(array);
        List<Integer> list = Arrays.asList(array);
        Flux.fromIterable(list);
        Stream<Integer> stream = list.stream();
        Flux.fromStream(stream);
        // 只有完成信号的空数据流
        Flux.just();
        Flux.empty();
        Mono.empty();
        Mono.justOrEmpty(Optional.empty());
        // 只有错误信号的数据流
        Flux.error(new Exception("error"));
        Mono.error(new Exception("error"));


        Flux.just(1, 2, 3, 4, 5, 6, 7).subscribe(System.out::print);
        System.out.println();
        Mono.just(1).subscribe(System.out::println);

        Flux.just(1, 2, 3, 4, 5).subscribe(System.out::println, System.err::print, () -> System.out.println("11111"));

        Flux.error(new Exception("error")).subscribe(System.out::println, System.err::println, () -> System.out.println("11111"));
    }

    private Flux<Integer> generateFluxFrom1To6() {
        return Flux.just(1, 2, 3, 4, 5, 6);
    }

    private Mono<Integer> generateMonoWithError() {
        return Mono.error(new Exception("error"));
    }

    @Test
    public void testViaStepVerifier() {
        StepVerifier.create(generateFluxFrom1To6())
                .expectNext(1, 2, 3, 4, 5, 6)   //测试下一个期望的数据元素
                .expectComplete()   //测试下一个元素是否为完成信号
                .verify();

        StepVerifier.create(generateMonoWithError())
                .expectErrorMessage("error")    //校验下一个元素是否为错误信号
                .verify();

        /**
         * map - 元素映射为新元素
         */
        StepVerifier.create(Flux.range(1, 6)
                .map(i -> i * i))
                .expectNext(1, 4, 9, 16, 25, 36)
                .verifyComplete();  //verifyComplete()相当于expectComplete().verify()。

        /**
         * flatMap - 元素映射为流
         */
        StepVerifier.create(Flux.just("flux", "mono")
                .flatMap(s -> Flux.fromArray(s.split("\\s*"))
                        .delayElements(Duration.ofSeconds(1))
                        .doOnNext(System.out::println)))
                .expectNextCount(8)
                .verifyComplete();

        /**
         * filter - 过滤
         */
        StepVerifier.create(Flux.range(1, 6)
                .filter(i -> i % 2 == 1)
                .map(i -> i * i))
                .expectNext(1, 9, 25)
                .verifyComplete();
    }

    private Flux<String> getZipDescFlux() {
        String desc = "Zip two sources together, that is to say wait for all the sources to emit one element and combine these elements once into a Tuple2.";
        return Flux.fromArray(desc.split("\\s+"));  // 1
    }

    @Test
    public void testSimpleOperators() throws InterruptedException {
        //定义一个CountDownLatch，初始为1，则会等待执行1次countDown方法后结束，不使用它的话，测试方法所在的线程会直接返回而不会等待数据流发出完毕；
        CountDownLatch countDownLatch = new CountDownLatch(1);
        Flux.zip(
                getZipDescFlux(),
                Flux.interval(Duration.ofMillis(200)))  //使用Flux.interval声明一个每200ms发出一个元素的long数据流；因为zip操作是一对一的，故而将其与字符串流zip之后，字符串流也将具有同样的速度；
                .subscribe(t -> System.out.println(t.getT1()), null, countDownLatch::countDown);    //zip之后的流中元素类型为Tuple2，使用getT1方法拿到字符串流的元素；定义完成信号的处理为countDown;
        countDownLatch.await(10, TimeUnit.SECONDS); //countDownLatch.await(10, TimeUnit.SECONDS)会等待countDown倒数至0，最多等待10秒钟。
    }


    private String getStringSync() {
        try {
            TimeUnit.SECONDS.sleep(2);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "Hello, Reactor!";
    }

    /**
     * 线程池
     *
     * @throws InterruptedException
     */
    @Test
    public void testSchedulers() throws InterruptedException {
        CountDownLatch countDownLatch = new CountDownLatch(1);
        Mono.fromCallable(this::getStringSync)  //使用fromCallable声明一个基于Callable的Mono；
                .subscribeOn(Schedulers.elastic())  //使用subscribeOn将任务调度到Schedulers内置的弹性线程池执行，弹性线程池会为Callable的执行任务分配一个单独的线程。
                .subscribe(System.out::println, null, countDownLatch::countDown);
        countDownLatch.await(10, TimeUnit.SECONDS);
    }

    @Test
    public void testErrorHandling() {
        Flux.range(1, 6)
                .map(i -> 10 / (i - 3))
//                .onErrorReturn(0)   //当发生异常时提供一个缺省值0
                .onErrorResume(e -> Mono.just(new Random().nextInt(6)))
                .map(i -> i * i)
                .subscribe(System.out::println, System.err::println);
    }
}
