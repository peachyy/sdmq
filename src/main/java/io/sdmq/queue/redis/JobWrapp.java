package io.sdmq.queue.redis;

import io.sdmq.queue.JobMsg;

/**
 * Created by Xs.Tao on 2017/7/24.
 */
public class JobWrapp extends JobMsg {

    private String buckedName;

    public String getBuckedName() {
        return buckedName;
    }

    public void setBuckedName(String buckedName) {
        this.buckedName = buckedName;
    }


}
