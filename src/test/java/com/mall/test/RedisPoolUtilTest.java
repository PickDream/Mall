package com.mall.test;

import com.mmall.common.RedisPool;
import com.mmall.util.RedisPoolUtil;
import org.junit.Test;
import redis.clients.jedis.Jedis;

public class RedisPoolUtilTest {
    @Test
    public void test(){
        Jedis jedis = RedisPool.getJedis();
        RedisPoolUtil.set("mykey","value");
        String value = RedisPoolUtil.get("mykey");
        System.out.println(value);
        RedisPoolUtil.setEx("keyex","valueex",60*10);

        RedisPoolUtil.expire("mykey",60*20);

        RedisPoolUtil.del("keyTest");

        System.out.println("完成");

    }
}
