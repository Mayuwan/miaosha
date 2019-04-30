package com.mayuwan.miaosha.common.redis;

public class GoodsKey extends BaseKeyPrefix {
    private GoodsKey(int expireSec, String prefix){
        super(expireSec,prefix);
    }
    private GoodsKey(String prefix){
        super(prefix);
    }

    private static final int LIST_CACHE_TIME = 30;

    public static GoodsKey LIST_CACHE = new GoodsKey(LIST_CACHE_TIME,"list_html");
    public static GoodsKey DETAIL_CACHE = new GoodsKey(LIST_CACHE_TIME,"detail_html");
//    public static OrderKey getByName = new OrderKey(0,"name");
}
