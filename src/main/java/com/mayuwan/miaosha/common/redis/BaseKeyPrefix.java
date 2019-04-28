package com.mayuwan.miaosha.common.redis;

public abstract class BaseKeyPrefix implements KeyPrefix {
    protected int expireSeconds;
    protected String keyPrefix;

    protected BaseKeyPrefix(int expireSeconds,String keyPrefix){
        this.expireSeconds = expireSeconds;
        this.keyPrefix = keyPrefix;
    }

    protected BaseKeyPrefix(String keyPrefix){
        this(0,keyPrefix);//永不过期
    }


    @Override
    public int expireSeconds(){
        return expireSeconds;
    }

    @Override
    public String getPrefix() {
        String className = getClass().getSimpleName();
        return className+"_"+keyPrefix;
    }
}
