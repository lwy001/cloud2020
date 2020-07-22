package com.atguigu.springcloud.redis.utils;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.RedisStringCommands;
import org.springframework.data.redis.connection.ReturnType;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.types.Expiration;
import org.springframework.data.redis.support.atomic.RedisAtomicLong;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.nio.charset.Charset;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author 李卫勇
 * @date 2020-07-20 23:27
 */
@Slf4j
@Component
public class RedisUtil {

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    public static final String UNLOCK_LUA;

    /**
     * 释放锁脚本，原子操作
     */
    static {
        StringBuilder sb = new StringBuilder();
        sb.append("if redis.call(\"get\",KEYS[1]) == ARGV[1] ");
        sb.append("then ");
        sb.append("    return redis.call(\"del\",KEYS[1]) ");
        sb.append("else ");
        sb.append("    return 0 ");
        sb.append("end ");
        UNLOCK_LUA = sb.toString();
    }

    /**
     * 默认过期时间，单位秒
     * */
    public static final long DEFAULT_EXPIRATION = 600;

    /**
     * 分割字符，默认[:]，使用:可用于rdm分组查看
     */
    private static final String KEY_SPLIT_CHAR = ":";

    /**
     * create by: ma bo
     * description: 设置Key的生命周期，指定缓存失效时间
     * create time: 2019/7/2 16:21
     * @Param: key
     * @Param: time
     * @return boolean
     */
    public boolean expireKey(String key, long time){
        try {
            if(time > 0){
                redisTemplate.expire(key,time, TimeUnit.SECONDS);
            }
        } catch (Exception e) {
        }
        return true;
    }

    /**
     * create by: ma bo
     * description: 设置key在指定日期过期
     * create time: 2019/7/2 16:51
     * @Param: key
     * @Param: date
     * @return boolean
     */
    public boolean expireKeyAt(String key, Date date){
        return redisTemplate.expireAt(key,date);

    }

    /**
     * create by: ma bo
     * description: 判断Key是否存在
     * create time: 2019/7/2 16:35
     * @Param: key
     * @return boolean
     */
    public boolean existKey(String key){
        return redisTemplate.hasKey(key);
    }

    /**
     * create by: ma bo
     * description: 查看Key的生命周期
     * create time: 2019/7/2 16:52
     * @Param: key
     * @return long
     */
    public long getKeyExpiration(String key){
        return redisTemplate.getExpire(key);
    }

    /**
     * 根据key获取缓存并转换为对应泛型
     *
     * @param key
     * @param clazz
     * @param <T>
     * @return
     */
    public <T> T getKey(String key, Class<T> clazz){
        Object val = redisTemplate.opsForValue().get(key);
        return null == val?null:JSON.parseObject(JSON.toJSONString(val), clazz);
    }

    /**
     * 根据key获取缓存
     *
     * @param key
     * @return
     */
    public Object getKey(String key){
        return redisTemplate.opsForValue().get(key);
    }

    /**
     * create by: ma bo
     * description: 写入缓存
     * create time: 2019/7/2 16:57
     * @Param: key
     * @Param: value
     * @return boolean
     */
    public boolean setKey(String key, Object value){
        try{
            redisTemplate.opsForValue().set(key, value);
        }catch (Exception e){
        }
        return true;
    }


    /**
     * create by: ma bo
     * description: 写入缓存并设置过期时间
     * create time: 2019/7/2 17:17
     * @Param: key
     * @Param: value
     * @Param: time,时间(秒) time要大于0 如果time小于等于0 将设置无限期
     * @return boolean
     */
    public boolean setKeyAndExpire(String key, Object value, long time){
        try{
            if(time > 0){
                redisTemplate.opsForValue().set(key,value,time,TimeUnit.SECONDS);
            }else{
                redisTemplate.opsForValue().set(key,value);
            }
        }catch (Exception e){
        }
        return true;
    }

    /**
     * 一次获取多个值
     *
     * @param keys
     * @return
     */
    public List<Object> multiGet(List<String> keys){
        return redisTemplate.opsForValue().multiGet(keys);
    }

    /**
     * create by: ma bo
     * description: 写入缓存并设置默认过期时间，10分钟
     * create time: 2019/7/3 15:59
     * @Param: key
     * @Param: value
     * @return boolean
     */
    public boolean setKeyAndDefaultExpire(String key, Object value){
        return setKeyAndExpire(key,value,DEFAULT_EXPIRATION);
    }

    /**
     * 更新缓存并返回旧值
     *
     * @param key
     * @param value
     * @return
     */
    public Object getAndSetKey(String key, Object value){
        try {
            return redisTemplate.opsForValue().getAndSet(key, value);
        }catch (Exception e){
            return null;
        }
    }

    /**
     * create by: ma bo
     * description: 删除缓存
     * create time: 2019/7/2 17:03
     * @Param: key
     * @return boolean
     */
    public boolean deleteKey(String key){
        try {
            redisTemplate.delete(key);
        }catch (Exception e){
        }
        return true;
    }

    /**
     * 尝试获取分布式锁
     *
     * @param key
     * @param requestId
     * @param expire
     * @return
     */
    public boolean tryLock(String key, String requestId, long expire) {
        try{
            RedisCallback<Boolean> callback =
                    (connection) -> connection.set(key.getBytes(Charset.forName("UTF-8")), requestId.getBytes(Charset.forName("UTF-8")), Expiration.from(expire, TimeUnit.SECONDS), RedisStringCommands.SetOption.SET_IF_ABSENT);
            return redisTemplate.execute(callback);
        } catch (Exception e) {
            log.error("设置分布式锁发生异常", e);
        }
        return false;
    }

    /**
     * 释放分布式锁
     *
     * @param key
     * @param requestId
     * @return
     */
    public boolean releaseLock(String key,String requestId) {
        RedisCallback<Boolean> callback =
                (connection) -> connection.eval(UNLOCK_LUA.getBytes(), ReturnType.BOOLEAN ,1, key.getBytes(Charset.forName("UTF-8")), requestId.getBytes(Charset.forName("UTF-8")));
        try {
            return redisTemplate.execute(callback);
        } catch (Exception e) {
            log.error("释放分布式锁发生异常", e);
        }
        return false;
    }

    /**
     * 设置如果该key不存在
     *
     * @param key
     * @param value
     * @return 是否设置成功
     */
    public boolean setIfAbsent(String key, Object value){
        return redisTemplate.opsForValue().setIfAbsent(key, value);
    }

    /**
     * 设置如果该值不存在, 并设置过期时间
     *
     * @param key
     * @param value
     * @param expire
     * @return
     */
    public void setIfAbsent(String key, Object value, long expire){
        redisTemplate.opsForValue().set(key, value, expire, TimeUnit.SECONDS);
    }

    /**
     * 自增
     *
     * @param key
     * @return
     */
    public Long incr(String key){
        RedisAtomicLong entityIdCounter = new RedisAtomicLong(key, redisTemplate.getConnectionFactory());
        Long increment = entityIdCounter.getAndIncrement();
        return increment;
    }

    /**
     * 自减
     *
     * @param key
     * @return
     */
    public Long decr(String key){
        RedisAtomicLong entityIdCounter = new RedisAtomicLong(key, redisTemplate.getConnectionFactory());
        long decrement = entityIdCounter.getAndDecrement();
        return decrement;
    }

    /**
     * 存入Hash结构数据
     *
     * @param key
     * @param hashKey
     * @param value
     */
    public void  setHash(String key, String hashKey, Object value){
        redisTemplate.opsForHash().put(key, hashKey, value);
    }

    /**
     * 不存在时存入Hash结构数据
     *
     * @param key
     * @param hashKey
     * @param value
     * @return
     */
    public Boolean setHashIfAbsent(String key, String hashKey, Object value){
        return redisTemplate.opsForHash().putIfAbsent(key, hashKey, value);
    }

    /**
     * 获取Hash结构数据
     *
     * @param key
     * @param hashKey
     * @return
     */
    public Object getHash(String key, String hashKey){
        return redisTemplate.opsForHash().get(key, hashKey);
    }

    /**
     * 根据key获取该key下所有Hash结构的数据
     *
     * @param key
     * @return
     */
    public List<Object> getAllHash(String key){
        return redisTemplate.opsForHash().values(key);
    }

    /**
     * 根据key获取该key下所有Hash结构的键值对
     *
     * @param key
     * @return
     */
    public Map<Object, Object> getAllHashEntry(String key){
        return redisTemplate.opsForHash().entries(key);
    }

    /**
     * 根据key获取该key下数据的个数
     *
     * @param key
     * @return
     */
    public Long getHashLength(String key){
        return redisTemplate.opsForHash().size(key);
    }

    /**
     * 删除Hash结构数据
     *
     * @param key
     * @param hashKey
     */
    public void deleteHash(String key, String hashKey){
        redisTemplate.opsForHash().delete(key, hashKey);
    }

}
