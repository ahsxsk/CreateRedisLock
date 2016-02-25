package com;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by MLS on 16/2/24.
 */

public class RedisConfig {
    private static String ADDR = "localhost";
    private static int PORT = 6379;
    private static JedisPool jedisPool = null;

    private static Lock lock = new ReentrantLock();
    private static Condition condition = lock.newCondition();
    /**
     * 初始化连接池
     */
    static {
        try {
            JedisPoolConfig config = new JedisPoolConfig();
            jedisPool = new JedisPool(config, ADDR, PORT);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取Redis实例
     * @return
     */
    public static Jedis getJedis() {
        try {
            lock.lock();
            if (jedisPool != null) {
                Jedis resource = jedisPool.getResource();
                return resource;
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            lock.unlock();
        }
    }

    /**
     * 释放jedis资源
     * @param jedis
     */

    public static void returnResource(final Jedis jedis) {
        if (jedis != null) {
            jedisPool.returnResource(jedis);
        }
    }
}
