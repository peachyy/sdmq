package io.sdmq.transactional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import io.sdmq.TestDelayQueue;
import io.sdmq.queue.redis.support.RedisSupport;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.Transaction;

/**
 * Created by Xs.Tao on 2017/8/1.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = TestDelayQueue.class)
@WebAppConfiguration
public class RedisTemplateTransactional {

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private RedisSupport support;

    @Test
    public void redisSupportTest() throws InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        for (int i = 0; i < 500; i++) {
            final int tmp = i;
            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    final Jedis jedis = support.getJedis();
                    new callback() {
                        @Override
                        public void exe(Jedis jedis) {
                            try {
                                Transaction transaction = jedis.multi();
                                transaction.set("abc" + tmp, Objects.toString(tmp));
//                                jedis.set("avd"+tmp,Objects.toString(tmp));
//                               // transaction.set("abc"+tmp, Objects.toString(tmp));
//                               // transaction.set("avd"+tmp,Objects.toString(tmp));
                                saveDb(tmp);
                                jedis.set("abc" + tmp, Objects.toString(tmp + "_2"));
//                                transaction.set("abc"+tmp,Objects.toString(tmp));
                                List<Object> lst = transaction.exec();
                                System.out.println(tmp);


                            } catch (Exception e) {
                                e.printStackTrace();
                            } finally {
                                jedis.close();
                            }
                        }
                    }.exe(jedis);

                }
            });
        }
        Thread.sleep(Integer.MAX_VALUE);
    }

    @Test
    //@Transactional
    public void test() throws Exception {
        System.out.println(redisTemplate);
        redisTemplate.watch("test");
        redisTemplate.multi();

        redisTemplate.opsForValue().set("test", "123");

        redisTemplate.opsForValue().set("test1", "456");

        redisTemplate.exec();

    }

    @Test
    public void test3() throws InterruptedException {
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxIdle(300);
        config.setMaxTotal(600);
        config.setMaxWaitMillis(1000 * 3);
        config.setMinIdle(200);
        config.setTestOnBorrow(true);
        final JedisPool pool = new JedisPool(config, "219.239.88.69", 6379, 300, "123", 7);

//       for(int i=0;i<200;i++){
//           Jedis jedis=pool.getResource();
//           jedis.set("aa",Objects.toString(i));
//           System.out.println(jedis);
//       }
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        for (int i = 0; i < 500; i++) {
            final int tmp = i;
            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    Jedis jedis = pool.getResource();
                    new callback() {
                        @Override
                        public void exe(Jedis jedis) {
                            try {
                                Transaction transaction = jedis.multi();
                                transaction.set("abc" + tmp, Objects.toString(tmp));
//                                jedis.set("avd"+tmp,Objects.toString(tmp));
//                               // transaction.set("abc"+tmp, Objects.toString(tmp));
//                               // transaction.set("avd"+tmp,Objects.toString(tmp));
                                saveDb(tmp);
//                                jedis.set("abc"+tmp,Objects.toString(tmp));
//                                transaction.set("abc"+tmp,Objects.toString(tmp));
                                List<Object> lst = transaction.exec();
                                System.out.println(tmp);

                            } catch (Exception e) {
                                System.out.println(e.getMessage());
                            } finally {
                                jedis.close();
                            }
                        }
                    }.exe(jedis);

                }
            });

        }
        ///pool.close();
        Thread.sleep(Integer.MAX_VALUE);
    }

    private void saveDb(int tmp) {
        if (tmp % 3 == 0) {
            throw new RuntimeException("失败父超时" + tmp);
        }

    }

    @Test
    public void test2() {
        System.out.println(11);
        List<Object> results = (List<Object>) redisTemplate.execute(new SessionCallback<List<Object>>() {
            @SuppressWarnings({"rawtypes", "unchecked"})
            public List<Object> execute(RedisOperations operations) {
                operations.multi();
                //new Jedis().multi();
                operations.opsForValue().set("abb", "233332");
                if (true) {
                    throw new eeee("xx");
                }
                operations.opsForValue().set("abb", "2");
//                operations.opsForValue().set("11", "22");
//                operations.opsForValue().get("11");
//                operations.opsForList().leftPush("aaa", 1);
//                operations.opsForList().range("aaa", 0l, 1l);
//                operations.opsForSet().add("bbbb", 12);
//                operations.opsForSet().members("bbbb");
                return operations.exec();
            }
        });
        for (Object o : results) {
            System.out.println(o);
        }
    }

    interface callback {

        void exe(Jedis jedis);
    }

    class eeee extends DataAccessException {

        public eeee(String msg) {
            super(msg);
        }
    }

}
