package com.mmall.util;

import com.mmall.common.RedisPool;
import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.Jedis;

/**
 * 封装API
 * */
@Slf4j
public class RedisPoolUtil {

    /**
     * 设置key的过期时间
     * @param key 键
     * @param exTime 时间，单位是秒
     * @return Integer reply, specifically: 1: the timeout was set. 0: the
     *         timeout was not set since the key already has an associated
     *         timeout (this may happen only in Redis versions < 2.1.3, Redis >=
     *         2.1.3 will happily update the timeout), or the key does not
     *         exist.
     */
    public static Long expire(String key,int exTime){
        Jedis jedis = null;
        Long result =null;
        try {
            jedis = RedisPool.getJedis();
            result = jedis.expire(key,exTime);
        }catch (Exception e){
            log.error("expire key:{} error",key,e);
            RedisPool.returnBrokenResource(jedis);
        }
        RedisPool.returnResoure(jedis);
        return result;
    }
    /**
     * @param key 键
     * @param value 值
     * @param exTime 时间单位为秒
     * */
    public static String setEx(String key,String value,int exTime){
        Jedis jedis = null;
        String result = null;
        //发生异常之后的处理
        try {
            jedis = RedisPool.getJedis();
            result = jedis.setex(key,exTime,value);
        }catch (Exception e){
            log.error("set key:{} value:{} error",key,value,e);
            RedisPool.returnBrokenResource(jedis);
        }
        RedisPool.returnResoure(jedis);
        return result;
    }


    public static String set(String key,String value){
        Jedis jedis = null;
        String result = null;
        //发生异常之后的处理
        try {
            jedis = RedisPool.getJedis();
            result = jedis.set(key,value);
        }catch (Exception e){
            log.error("set key:{} value:{} error",key,value,e);
            RedisPool.returnBrokenResource(jedis);
        }
        RedisPool.returnResoure(jedis);
        return result;
    }

    public static String get(String key){
        Jedis jedis = null;
        String result = null;
        //发生异常之后的处理
        try {
            jedis = RedisPool.getJedis();
            result = jedis.get(key);
        }catch (Exception e){
            log.error("get key:{} ",key,e);
            RedisPool.returnBrokenResource(jedis);
        }
        RedisPool.returnResoure(jedis);
        return result;
    }


    /**
     * @param key 待删除的键
     * @return Integer reply, specifically: an integer greater than 0 if one or
     *          more keys were removed 0 if none of the specified key existed
     * */
    public static Long del(String key){
        Jedis jedis = null;
        Long result = null;
        try {
            jedis = RedisPool.getJedis();
            result = jedis.del(key);
        }catch (Exception e){
            log.error("get key:{} ",key,e);
            RedisPool.returnBrokenResource(jedis);
        }
        RedisPool.returnResoure(jedis);
        return result;
    }
}
