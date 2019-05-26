package com.mmall.common;

import com.mmall.util.PropertiesUtil;
import redis.clients.jedis.*;
import redis.clients.util.Hashing;
import redis.clients.util.Sharded;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * */
public class RedisShardedPool {
    //连接池
    private static ShardedJedisPool pool;
    //最大连接数
    private static Integer maxTotal= Integer.parseInt(PropertiesUtil.getProperty("redis.max.total","20"));
    //最大空闲连接个数
    private static Integer maxIdle=Integer.parseInt(PropertiesUtil.getProperty("redis.max.idle","10"));
    //最小空闲连接个数
    private static Integer minIdle=Integer.parseInt(PropertiesUtil.getProperty("redis.min.idle","2"));
    //是否先borrow一个Jedis实例，验证
    private static Boolean testOnBorrow=Boolean.parseBoolean(PropertiesUtil.getProperty("redis.test.borrow","true"));
    //是否在归还之前验证以下
    private static Boolean testOnReturn=Boolean.parseBoolean(PropertiesUtil.getProperty("redis.test.return","true"));

    private static String redis1Ip = PropertiesUtil.getProperty("redis1.ip");
    private static Integer redis1Port=Integer.parseInt(PropertiesUtil.getProperty("redis1.port"));
    private static String redis2Ip = PropertiesUtil.getProperty("redis2.ip");
    private static Integer redis2Port = Integer.parseInt(PropertiesUtil.getProperty("redis2.port"));


    /**
     * Redis 集群配置
     * */
    private static void initPool(){
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxTotal(maxTotal);
        config.setMaxIdle(maxIdle);
        config.setMinIdle(minIdle);
        config.setTestOnBorrow(testOnBorrow);
        config.setTestOnReturn(testOnReturn);
        //连接耗尽的时候是否阻塞
        config.setBlockWhenExhausted(true);
        //默认两秒的超时时间
        JedisShardInfo info1 = new JedisShardInfo(redis1Ip,redis1Port);;
        JedisShardInfo info2 = new JedisShardInfo(redis2Ip,redis2Port);

        List<JedisShardInfo> jedisShardInfos = new ArrayList<>(2);
        jedisShardInfos.add(info1);
        jedisShardInfos.add(info2);

        //创建ShardedJedisPool,
        //MURMUR_HASH 对应一致性算法
        pool = new ShardedJedisPool(config,jedisShardInfos,
                Hashing.MURMUR_HASH, Sharded.DEFAULT_KEY_TAG_PATTERN);


    }

    /**
     * 初始化集群配置方法
     * */
    static {
        initPool();
    }

    public static ShardedJedis getJedis(){
        return pool.getResource();
    }

    //归还连接
    public static void returnResoure(ShardedJedis jedis){
        pool.returnResource(jedis);
    }

    //归还损坏的连接
    public static void returnBrokenResource(ShardedJedis jedis){
        pool.returnBrokenResource(jedis);
    }


}
