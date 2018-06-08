package io.sdmq.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

/**
 * <pre>
 *     -DmachineId=num 机器标识
 * </pre>
 * Created by Xs.Tao on 2017/9/18.
 */
public class JobIdGenerator {

    public static final Logger    LOGGER           = LoggerFactory.getLogger(JobIdGenerator.class);
    public static final int       DATACENTER       = 2;
    public static final int       DEFAULT_MACHINED = 1;
    private static      SnowFlake snowFlake        = null;

    static {
        int m = machinedId();
        LOGGER.info(" machined {}", m);
        snowFlake = new SnowFlake(DATACENTER, m);
    }

    private static int machinedId() {
        //通过获取IP地址最后一位来获取
        String MACHINED = System.getProperty("machineId");
        if (!StringUtils.isEmpty(MACHINED)) {
            try {
                return Integer.parseInt(MACHINED);
            } catch (Exception e) {
                return DEFAULT_MACHINED;
            }
        }
        return DEFAULT_MACHINED;
    }

    public static long getLongId() {
        return snowFlake.nextId();
    }

    public static String getStringId() {
        return String.valueOf(snowFlake.nextId());
    }

//    public static void main(String[] args) {
//       final Map<String,Object> c= Maps.newHashMap();
//       final Map<String,Object> c1=Maps.newHashMap();
//       final AtomicLong atomicLong=new AtomicLong(0);
//        for(int i=0;i<20;i++){
//            new Thread(new Runnable() {
//                @Override
//                public void run() {
//                    for(int i=0;i<5000;i++){
//                        String k=getStringId();
//                        atomicLong.incrementAndGet();
//                        System.out.println(k+"=="+c.containsKey(k)+"--"+atomicLong.get());
//                        if(!c.containsKey(k)){
//                            c.put(k,i);
//                        }else{
//                            throw new RuntimeException(String.format("id %s重复了",k));
//                        }
//
//
//                    }
//                    c1.put(Thread.currentThread().getName(),"");
//                }
//            }).start();
//        }
//        while(c1.size()>=20){
//            System.out.println(c.size());
//        }
//        try {
//            Thread.sleep(5000000L);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//    }
}
