package io.sdmq.util;

/**
 * Created by Xs.Tao on 2017/7/18.
 */
public enum Status {
    WaitPut,//待加入
    Delay,//已经进入延时队列
    Ready,//已经出了延时队列 客户端可以方法此数据
    Finish,//客户端已经处理完数据了
    Delete,//客户端已经把数据删除了
    Restore,//手动恢复重发状态/或者是在实时队列中验证时间出现异常 再次放入buck中
    ConsumerFailRestore//消费失败

}
