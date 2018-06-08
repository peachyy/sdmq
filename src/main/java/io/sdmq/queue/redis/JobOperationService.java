package io.sdmq.queue.redis;

import java.util.List;

import io.sdmq.queue.JobMsg;
import io.sdmq.util.Status;

/**
 * Created by Xs.Tao on 2017/7/19.
 */
public interface JobOperationService {

    /**
     * 获取Job元数据
     */
    JobMsg getJob(String jobId);

    /**
     * 添加Job到元数据池
     */
    void addJobToPool(JobMsg jobMsg);

    /**
     * 删除元数据此任务
     */
    void removeJobToPool(String jobId);

    /**
     * 更新元任务池任务的状态
     */
    void updateJobStatus(String jobId, Status status);

    /**
     * 根据JobId删除元数据
     */
    void deleteJobToPool(String jobId);

    /**
     * 加一个Job到指定Bucket
     */
    void addBucketJob(String bucketName, String JobId, double score);

    /**
     * 从指定Bucket删除一个Job
     */
    void removeBucketKey(String bucketName, String jobId);

    /**
     * 添加一个Job到 可执行队列
     */
    void addReadyTime(String readyName, String jobId);


    /**
     * 获取一个实时队列中的第一个数据
     */
    String getReadyJob();

    /**
     * 获取指定个数实时队列中的数据 不是用的POP方式 需要手動刪除
     */
    List<String> getReadyJob(int size);

    /**
     * 刪除实时队列中的一个数据
     */
    boolean removeReadyJob(String jobId);

    /**
     * 获取bucket中最顶端的一个Job
     */
    String getBucketTop1Job(String bucketName);

    /**
     * 批量获取顶端数据 只获取满足条件的数据 最多<code>size</code>行
     */
    List<String> getBucketTopJobs(String bucketName, int size);

    void clearAll();
}
