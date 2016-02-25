package com;

import redis.clients.jedis.Jedis;
import com.RedisConfig;
public class Main {

    private static Jedis jedis;

    public static void main(String[] args) {
	// write your code here
      // jedis = new Jedis("localhost", 6379);
      //  jedis.set("firstJedis", "It's my first Jedis");
        jedis = RedisConfig.getJedis();
        jedis.set("testPool", "Yes");
        RedisConfig.returnResource(jedis);
    }
}
