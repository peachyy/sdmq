package io.sdmq.common.autoconfigure;

import com.alibaba.rocketmq.client.producer.DefaultMQProducer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.Assert;

/**
 * Created by Xs.Tao on 2017/7/20.
 */
@Configuration
@EnableConfigurationProperties(RocketMQProperties.class)
@ConditionalOnClass(value = {DefaultMQProducer.class})
public class RocketmqAutoConfiguration {

    @Autowired
    private RocketMQProperties properties;

    @Bean(initMethod = "init", destroyMethod = "close")
    public MessageProducer newMessageProducer() {
        Assert.notNull(properties.getNamesrvAddr(), "请正确配置NamesrvAddr");
        MessageProducer producer = new MessageProducer();
        producer.setNamesrvAddr(properties.getNamesrvAddr());
        return producer;
    }

}
