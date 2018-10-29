package io.sdmq;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

import io.sdmq.util.StartGetReady;

/**
 * Created by Xs.Tao on 2017/7/18.
 */
@SpringBootApplication
public class Bootstarp {

    public static void main(String[] args) {
        StartGetReady.ready();
        ConfigurableApplicationContext context =
                new SpringApplicationBuilder(Bootstarp.class)
                        .run(args);


    }

}
