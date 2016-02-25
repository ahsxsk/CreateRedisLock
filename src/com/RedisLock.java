package com;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;

import java.util.UUID;

/**
 * Created by MLS on 16/2/25.
 */
public class RedisLock {
    /**
     * 申请锁
     * @param jedis redis连接
     * @param lockName 锁名称
     * @param timeout 超时时间,ms
     * @return
     */
    public String acquireLock(Jedis jedis, String lockName, long timeout) {
        String identifier = UUID.randomUUID().toString(); // 锁随机标识符
        long expired = System.currentTimeMillis() + timeout;
        while (System.currentTimeMillis() < expired) {
            if (jedis.setnx("lock" + lockName, identifier) == 1) { //尝试获取锁
                return identifier;
            }
        }
        return "";
    }

    /**
     * 释放锁
     * @param jedis redis连接
     * @param lockName 锁名称
     * @param identifier 锁标识符
     * @return
     */
    public boolean releaseLock(Jedis jedis, String lockName, String identifier) {
        Pipeline pipeline = jedis.pipelined();
        lockName = "lock:" + lockName;
        while (true) {
            try {
                pipeline.watch(lockName);
                if (pipeline.get(lockName).equals(identifier)) { //检查线程是否仍然持有锁
                    pipeline.multi(); //释放锁
                    pipeline.del(lockName);
                    if (pipeline.exec() != null) {
                        return true;
                    }
                }
                break;
            } catch (Exception e) { //其他客户端修改了锁
                System.out.println("release failed");
            }
            return false;
        }
        return false;
    }
}
