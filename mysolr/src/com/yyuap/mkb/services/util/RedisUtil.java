package com.yyuap.mkb.services.util;

import java.util.concurrent.locks.ReentrantLock;

import javax.servlet.ServletContextEvent;

import org.apache.commons.lang.StringUtils;

import com.yyuap.mkb.log.MKBLogger;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class RedisUtil {
//	 private final static String url = "direct://10.3.13.7:6868?poolName=mypool&poolSize=100&password=yywl_ydpt";
//	 private static Jedis jedis = null;
//	 public static Jedis initRedis(){
//		 //连接本地的 Redis 服务
//		 if(jedis == null){
//			 jedis = new Jedis("10.3.13.7",6868);
//			 jedis.auth("yywl_ydpt");
//			 MKBLogger.info("新创建");
//		 }
//		 MKBLogger.info("连接成功");
//	     //查看服务是否运行
//	     MKBLogger.info("服务正在运行: "+jedis.ping());
//	     
//	     return jedis;
//	 }
	protected static ReentrantLock lockPool = new ReentrantLock();  
    protected static ReentrantLock lockJedis = new ReentrantLock();  
  
    //Redis服务器IP  
    private static String ADDR_ARRAY = "10.3.13.7";  
  
    //Redis的端口号  
    private static int PORT = 6868;  
  
    //访问密码  
    private static String AUTH = "yywl_ydpt";  
    //可用连接实例的最大数目，默认值为8；  
    //如果赋值为-1，则表示不限制；如果pool已经分配了maxActive个jedis实例，则此时pool的状态为exhausted(耗尽)。  
    private static int MAX_ACTIVE = 100;  
  
    //控制一个pool最多有多少个状态为idle(空闲的)的jedis实例，默认值也是8。  
    private static int MAX_IDLE = 100;  
  
    //等待可用连接的最大时间，单位毫秒，默认值为-1，表示永不超时。如果超过等待时间，则直接抛出JedisConnectionException；  
    private static int MAX_WAIT = -1;  
  
    //超时时间  
    private static int TIMEOUT = 10000;  
  
    //在borrow一个jedis实例时，是否提前进行validate操作；如果为true，则得到的jedis实例均是可用的；  
    private static boolean TEST_ON_BORROW = false;  
  
    private static JedisPool jedisPool = null;  
  
    /** 
     * redis过期时间,以秒为单位 
     */  
    public final static int EXRP_HOUR = 60 * 60;            //一小时  
    public final static int EXRP_DAY = 60 * 60 * 24;        //一天  
    public final static int EXRP_MONTH = 60 * 60 * 24 * 30; //一个月  
  
    /** 
     * 初始化Redis连接池 
     */  
    private static void initialPool() {  
        try {  
            JedisPoolConfig config = new JedisPoolConfig();  
            config.setMaxTotal(MAX_ACTIVE);  
            config.setMaxIdle(MAX_IDLE);  
            config.setMaxWaitMillis(MAX_WAIT);  
            config.setTestOnBorrow(TEST_ON_BORROW);  
            jedisPool = new JedisPool(config, ADDR_ARRAY.split(",")[0], PORT, TIMEOUT, AUTH);  
        } catch (Exception e) {  
            try {  
                //如果第一个IP异常，则访问第二个IP  
                JedisPoolConfig config = new JedisPoolConfig();  
                config.setMaxTotal(MAX_ACTIVE);  
                config.setMaxIdle(MAX_IDLE);  
                config.setMaxWaitMillis(MAX_WAIT);  
                config.setTestOnBorrow(TEST_ON_BORROW);  
                jedisPool = new JedisPool(config, ADDR_ARRAY.split(",")[1], PORT, TIMEOUT, AUTH);  
            } catch (Exception e2) {  
            }  
        }  
    }  
  
    /** 
     * 在多线程环境同步初始化 
     */  
    private static synchronized void poolInit() {  
        if (jedisPool == null) {    
            initialPool();  
        }  
    }  
  
      
    /** 
     * 同步获取Jedis实例 
     * @return Jedis 
     */  
    public synchronized static Jedis getJedis() {    
        if (jedisPool == null) {    
            poolInit();  
        }  
        Jedis jedis = null;  
        try {    
            if (jedisPool != null) {    
                jedis = jedisPool.getResource();   
            }  
        } catch (Exception e) {    
        }finally{  
            returnResource(jedis);  
        }  
        return jedis;  
    }    
  
    /** 
     * 释放jedis资源 
     * 
     * @param jedis 
     */  
    public static void returnResource(final Jedis jedis) {  
        if (jedis != null && jedisPool != null) {  
            jedisPool.returnResource(jedis);  
        }  
    }  
  
    /** 
     * 设置 String 
     * 
     * @param key 
     * @param value 
     */  
    public synchronized static void setString(String key, String value) {  
        try {  
            value = StringUtils.isEmpty(value) ? "" : value;  
            getJedis().set(key, value);  
        } catch (Exception e) {  
        }  
    }  
  
    /** 
     * 设置 过期时间 
     * 
     * @param key 
     * @param seconds 以秒为单位 
     * @param value 
     */  
    public synchronized static void setString(String key, int seconds, String value) {  
        try {  
            value = StringUtils.isEmpty(value) ? "" : value;  
            getJedis().setex(key, seconds, value);  
        } catch (Exception e) {  
        }  
    }  
  
    /** 
     * 获取String值 
     * 
     * @param key 
     * @return value 
     */  
    public synchronized static String getString(String key) {  
        if (getJedis() == null || !getJedis().exists(key)) {  
            return null;  
        }  
        return getJedis().get(key);  
    } 
	
	 public static void main(String[] args) {
//		 RedisUtil.setString("testpjf", "testpjf");
		 MKBLogger.info(RedisUtil.getString("667eb1cb-8776-445f-bb38-5e57a0ca336c"));
	}
	 
}
