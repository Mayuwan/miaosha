package com.mayuwan.miaosha.common.redis;

public interface KeyPrefix {

     int expireSeconds();
     String getPrefix();

}
