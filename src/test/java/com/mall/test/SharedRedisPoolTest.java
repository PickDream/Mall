package com.mall.test;

import com.mmall.common.RedisShardedPool;
import org.junit.Test;
import redis.clients.jedis.ShardedJedis;

public class SharedRedisPoolTest {

    @Test
    public void test(){
        ShardedJedis jedis = RedisShardedPool.getJedis();
        for (int i=0;i<10;i++){
            jedis.set("key-"+i,"value"+i);
        }
        RedisShardedPool.returnResoure(jedis);
        System.out.println("program is end!!!!");
    }
}
