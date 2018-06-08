package io.sdmq;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import io.sdmq.util.StartGetReady;

/**
 * Created by Xs.Tao on 2017/7/20.
 */
@SpringBootApplication
public class TestDelayQueue {

    public static void main(String[] args) {
        StartGetReady.ready();
        ApplicationContext ctx = SpringApplication.run(TestDelayQueue.class, args);
    }
}
