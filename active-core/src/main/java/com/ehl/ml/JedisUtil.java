package com.ehl.ml;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;


public class JedisUtil {
    private static JedisPool pool;
    private static String ip;
    private static String port;

    public static void init(String ip, String port) {
		JedisUtil.ip = ip;
		JedisUtil.port = port;
		getPool();
    }

    private static JedisPool getPool() {
        if (pool == null) {
            JedisPoolConfig config = new JedisPoolConfig();
            config.setMaxTotal(500);
            config.setMaxIdle(5);
            config.setMaxWaitMillis(1000 * 60);

            config.setTestOnBorrow(true);
            config.setTestOnReturn(true);
            try {
            	String port = JedisUtil.port;
                pool = new JedisPool(config, JedisUtil.ip, Integer.valueOf(port), 100000);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return pool;
    }

    public static void returnResource(Jedis jedis) {
        try {
            if (jedis != null) {
                pool.returnResource(jedis);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void returnBrokenResource(Jedis jedis) {
        try {
            if (jedis != null) {
                pool.returnBrokenResource(jedis);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Jedis getJedis() {
        if (pool == null) {
            getPool();
            if (pool != null) {
                return pool.getResource();
            } else {
                return null;
            }
        } else {
            return pool.getResource();
        }
    }

    public static void closePool() {
        if (pool != null) {
            pool.destroy();
        }
    }

}
