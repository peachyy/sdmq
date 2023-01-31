package io.sdmq.common.autoconfigure;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;

import com.alibaba.rocketmq.client.exception.MQBrokerException;
import com.alibaba.rocketmq.client.exception.MQClientException;
import com.alibaba.rocketmq.client.producer.DefaultMQProducer;
import com.alibaba.rocketmq.client.producer.SendResult;
import com.alibaba.rocketmq.common.message.Message;
import com.alibaba.rocketmq.remoting.exception.RemotingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.io.Closeable;
import java.io.IOException;
import java.io.Serializable;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import io.sdmq.queue.JobMsg;
import io.sdmq.queue.core.Job;
import io.sdmq.queue.redis.RedisQueueImpl;
import io.sdmq.util.FastJsonConvert;


public class MessageProducer implements Closeable {

    public static final Logger            LOGGER    = LoggerFactory.getLogger(RedisQueueImpl.class);
    public static final ExecutorService   EXECUTORS = Executors.newFixedThreadPool(2);
    private static      DefaultMQProducer PRODUCER;
    private             String            namesrvAddr;
    private String groupName;

    /**
     * @return
     */
    public static boolean send(Job msg) {
        Assert.notNull(msg, "参数错误");
        Message message = new Message();
        message.setTopic(msg.getTopic());
        if (!StringUtils.isEmpty(msg.getSubtopic())) {
            message.setTags(msg.getSubtopic());
        }
        message.setKeys(msg.getBizKey());
        Serializable data = msg.getBody();
        if (data != null) {
            message.setBody(((String) data).getBytes(Charset.forName("UTF-8")));
        } else {
            message.setBody("".getBytes(Charset.forName("UTF-8")));
        }
        if(!StringUtils.isEmpty(msg.getExtendData())){
            Map<String, Object> map = FastJsonConvert.convertJSONMap(msg.getExtendData());
            if(!CollectionUtils.isEmpty(map)){
                for(Map.Entry<String,Object> entry:map.entrySet()){
                    message.putUserProperty(entry.getKey(),Objects.toString(entry.getValue()));
                }
            }
        }

        try {
            SendResult send = PRODUCER.send(message);
        } catch (MQClientException | MQBrokerException | RemotingException | InterruptedException e) {
            LOGGER.error(String.format("消息发送失败[%s]", message.toString()), e);
            return false;
        }
        return true;
    }

    //guava异步发送mq
    public static void sendAsyncMessage(final JobMsg job) {

        ListeningExecutorService guavaExecutor = MoreExecutors.listeningDecorator(EXECUTORS);

        final ListenableFuture<Boolean> listenableFuture = guavaExecutor.submit(new Callable<Boolean>() {

            @Override
            public Boolean call() throws Exception {
                return MessageProducer.send(job);
            }
        });
        Futures.addCallback(listenableFuture, new FutureCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean mqMessageStatus) {
            }

            @Override
            public void onFailure(Throwable throwable) {
                LOGGER.error(throwable.getMessage());
            }
        });
    }

    public String getNamesrvAddr() {
        return namesrvAddr;
    }

    public void setNamesrvAddr(String namesrvAddr) {
        this.namesrvAddr = namesrvAddr;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    protected void init() {
        if (PRODUCER == null) {
            PRODUCER = new DefaultMQProducer(Objects.toString(groupName,"Producer"));
            PRODUCER.setNamesrvAddr(namesrvAddr);
            try {
                PRODUCER.start();
            } catch (MQClientException e) {
                LOGGER.error("消息发送端初始化失败", e);
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * 关闭消息发送端，同时释放资源，由容器自动处理，程序中不能调用此方法
     */
    @Override
    public void close() throws IOException {
        if (PRODUCER != null) {
            LOGGER.info("shutdowing mq...");
            PRODUCER.shutdown();
        }
    }
}
