package io.sdmq;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.Calendar;
import java.util.Date;
import java.util.Random;

import io.sdmq.queue.JobMsg;
import io.sdmq.queue.redis.JobWrapp;
import io.sdmq.queue.redis.RedisQueueImpl;
import io.sdmq.util.JobIdGenerator;

/**
 * Created by Xs.Tao on 2017/7/19.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = TestDelayQueue.class)
@WebAppConfiguration
public class FixTest {

    @Autowired
    private RedisQueueImpl redisQueue;

    @Test
    public void pushTest() {
        for (int i = 0; i < 500; i++) {
            long     time     = 1000 * (60 * new Random().nextInt(100) + 1);
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(System.currentTimeMillis() + time);
            int    hour = calendar.get(Calendar.HOUR_OF_DAY);
            int    min  = calendar.get(Calendar.MINUTE);
            int    src  = calendar.get(Calendar.SECOND);
            JobMsg job  = new JobWrapp();
            job.setBody(String.format("{你应该在 %s 运行}", hour + ":" + min + ":" + src));
            job.setTopic("test1".concat(new Date().getSeconds() + ""));
            job.setDelay(time);
            job.setId(JobIdGenerator.getStringId());
            redisQueue.push(job);

//         try {
//             Thread.sleep(1000L);
//         } catch (InterruptedException e) {
//             e.printStackTrace();
//         }
        }
    }
}
