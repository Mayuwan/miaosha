package com.mayuwan.miaosha.common.redis;

import com.alibaba.fastjson.JSON;
import com.mayuwan.miaosha.vo.GoodsVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import redis.clients.jedis.*;

import java.util.ArrayList;
import java.util.List;

@Service
public class RedisService {
    @Autowired
    RedisConfig redisConfig;

    @Autowired
    JedisPool jedisPool;


    @Bean
    public JedisPool JedisPoolFactory(){
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        jedisPoolConfig.setMaxIdle(redisConfig.getPoolMaxIdle());
        jedisPoolConfig.setMaxTotal(redisConfig.getPoolMaxTotal());
        jedisPoolConfig.setMaxWaitMillis(redisConfig.getPoolMaxWait()*1000);
        JedisPool js=  new JedisPool(jedisPoolConfig,redisConfig.getHost(),redisConfig.getPort(),redisConfig.getTimeout()*1000,redisConfig.getPassword(),0);
        return js;
    }


    public <T> T get(KeyPrefix prefix,String key,Class clx){
        Jedis jedis= null;
        try{
            jedis= jedisPool.getResource();
            if(prefix == null || StringUtils.isEmpty(key) || clx == null){
                return null;
            }
            String val = jedis.get(prefix.getPrefix()+key);
            T  obj = StringToBean(val,clx);
            return obj;
        }finally {   //连接池要释放
            returnToPool(jedis);
        }

    }
    /**基本类型*/
//    @SuppressWarnings("unchecked")
    public static  <T> T StringToBean(String val,Class clx) {
        if(StringUtils.isBlank(val) || clx == null){
            return null;
        }
        if(clx == int.class || clx == Integer.class){
            return (T)Integer.valueOf(val);
        } else if(clx == double.class || clx ==Double.class){
            return (T)Double.valueOf(val);
        } else if(clx == boolean.class || clx ==Boolean.class){
            return (T)Boolean.valueOf(val);
        } else if(clx == long.class || clx == Long.class){
            return (T)Long.valueOf(val);
        } else if(clx == String.class){
            return (T)val;
        }
        else{
            return (T)JSON.toJavaObject(JSON.parseObject(val),clx);
        }
    }

    public <T> void set(KeyPrefix prefix,String key,T value){
        Jedis jedis = null;
        try{
             jedis= jedisPool.getResource();
            String val = BeanToString(value);
            int seconds = prefix.expireSeconds();
            if(seconds <= 0){
                jedis.set(prefix.getPrefix()+key,val);
            }
            else{
                jedis.setex(prefix.getPrefix()+key,seconds,val);
            }
        }finally {
            returnToPool(jedis);
        }

    }

    public Long decr(KeyPrefix prefix,String key){
        Jedis jedis = null;
        try{
            jedis= jedisPool.getResource();
            int seconds = prefix.expireSeconds();

            return jedis.decr(prefix.getPrefix()+key);

        }finally {
            returnToPool(jedis);
        }
    }

    public Long incr(KeyPrefix prefix,String key){
        Jedis jedis = null;
        try{
            jedis= jedisPool.getResource();
            int seconds = prefix.expireSeconds();
            return jedis.incr(prefix.getPrefix()+key);

        }finally {
            returnToPool(jedis);
        }
    }


    public static <T> String BeanToString(T value) {
        Class clx = value.getClass();
        if(clx == int.class){
            return ""+value;
        } else if(clx == double.class ){
            return ""+value;
        } else if(clx == boolean.class ){
            return ""+value;
        } else if(clx == long.class ){
            return ""+value;
        } else if(clx == String.class){
            return (String)value;
        }
        else{
            return JSON.toJSONString(value);
        }
    }

    private void returnToPool(Jedis jedis){
        if(jedis != null) {
            jedis.close();
        }
    }

    public boolean exists(KeyPrefix prefix, String key) {
        Jedis jedis = null;
        try{
            jedis= jedisPool.getResource();
            return jedis.exists(prefix.getPrefix()+key);

        }finally {
            returnToPool(jedis);
        }
    }

    public long delete(KeyPrefix prefix, String key) {
        Jedis jedis = null;
        try{
            jedis= jedisPool.getResource();
            return jedis.del(prefix.getPrefix()+key);
        }finally {
            returnToPool(jedis);
        }
    }
    public boolean delete(KeyPrefix prefix) {
        if(prefix == null) {
            return false;
        }
        List<String> keys = scanKeys(prefix.getPrefix());
        if(keys==null || keys.size() <= 0) {
            return true;
        }
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            jedis.del(keys.toArray(new String[0]));
            return true;
        } catch (final Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            if(jedis != null) {
                jedis.close();
            }
        }
    }

    public List<String> scanKeys(String key) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            List<String> keys = new ArrayList<String>();
            String cursor = "0";
            ScanParams sp = new ScanParams();
            sp.match("*"+key+"*");
            sp.count(100);
            do{
                ScanResult<String> ret = jedis.scan(cursor, sp);
                List<String> result = ret.getResult();
                if(result!=null && result.size() > 0){
                    keys.addAll(result);
                }
                //再处理cursor
                cursor = ret.getStringCursor();
            }while(!cursor.equals("0"));
            return keys;
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }


}
