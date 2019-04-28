package com.mayuwan.miaosha.common.redis;

public class OrderKey extends BaseKeyPrefix {
    private OrderKey(int expireSec,String prefix){
        super(expireSec,prefix);
    }
    private OrderKey(String prefix){
        super(prefix);
    }

//    public static OrderKey getById = new OrderKey(0,"id");
//    public static OrderKey getByName = new OrderKey(0,"name");
}
