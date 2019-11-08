package com.atgla.dw;

import redis.clients.jedis.Jedis;

public class TestRedis {
    public static void main(String[] args) {
        //连接本地 redis服务器
        Jedis jedis = new Jedis("node101", 6379);
        //查看服务器是否运行  打印出 pong 表示ok
        System.out.println("connect is ok ==========="+jedis.ping());

    }
}
