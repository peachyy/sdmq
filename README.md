# sdmq
is a simple delay message queue， based on redis and kotlin 

设计 <a href="https://www.cnblogs.com/peachyy/p/7398430.html" target="_blank">https://www.cnblogs.com/peachyy/p/7398430.html</a>

`一个简单、稳定、可扩展的延迟消息队列` 

# 运行模式

* 支持 master,slave （HA）需要配置`sdmq.registry.serverList` zk集群地址列表
* 支持 cluster 会涉及到分布式锁竞争 效果不是很明显  分布式锁采用`redis`的 `setNx`实现
* StandAlone 

推荐使用master slave的模式

##### Usage

#### 消息体 

以JSON数据格式参数 目前只提供了`http`协议


* body                 业务消息体
* delay                延时毫秒 距`createTime`的间隔毫秒数
* id                   任务ID 系统自动生成 任务创建成功返回
* status               状态 默认不填写  
* topic                标题
* subtopic             保留字段 
* ttl                  保留字段
* createTime           创建任务时间 非必填 系统默认

#### 添加任务
 

````
/push  
  POST application/json

{"body":"{ffff}","delay":56600,"id":"20","status":0,"topic":"ces","subtopic":"",ttl":12}
````

#### 删除任务

 删除任务 需要记录一个JobId
 
````
/delete?jobId=xxx
   GET
````
#### 恢复单个任务

 用于任务错乱 脑裂情况 根据日志恢复任务
 
````
/reStoreJob?JobId=xxx
   GET
````
#### 恢复所有未完成的任务 

  根据日志恢复任务
 
 ````
 /reStore?expire=true
    GET
 ````
 
 参数`expire` 表示是否需要恢复已过期还未执行的数据
 
#### 清空队列数据 

  根据日志中未完成的数据清空队列中全部数据
  
  `清空之后 会删除缓存中的所有任务`
 ````
 /clearAll
    GET
 ````
 

   
#### 客户端获取队列方式

目前默认实现了`rocketmq`的推送方式。暂时就不用自己去实现推拉数据了。直接强依赖MQ。

##### 消息体中消息与`rocketmq`消息字段对应关系

sdmq        | rocketMQ | 备注|
---               | ---      |---          
topic    | topic    |     |         
subtopic | subtopic |      |    
body    | 消息内容   |   消息内容   |    
         


## 后期优化

* 分区(buck)支持动态设置
* redis与数据库数据一致性的问题 （`重要`）
* 实现自己的推拉机制
* 支持可切换实现方式 当前强依赖redis 只有这么1个实现
* 支持Web控制台管理队列
* 实现消息消费`TTL`机制 

定位是后期会改为基于`kotlin` `java`太多麻烦事了
## 测试
 需要配置好数据库地址和redis的地址 如果不是单机模式 也需要配置好zookeep
 
 运行测试类`io.sdmq.FixTest`添加任务到队列中 
 
 启动`Bootstarp`消费前面添加数据 为了方便查询效果 默认的消费方式是`consoleCQ` 控制台输出

## 更新日志

 * 2017年11月21日14:34:39 支持`restful`清空队列数据 
 * 2018年03月19日14:26:56 支持配置消费方式 默认为`jmsCQ` 在不修改代码的情况下覆盖方式 `-DClassName=xxxx`
   
   
   
