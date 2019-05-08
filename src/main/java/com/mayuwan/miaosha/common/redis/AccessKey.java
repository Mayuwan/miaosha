package com.mayuwan.miaosha.common.redis;

public class AccessKey extends BaseKeyPrefix {
    private AccessKey(int expireSec, String prefix){
        super(expireSec,prefix);
    }
    private AccessKey(String prefix){
        super(prefix);
    }

    private static final int LIST_CACHE_TIME = 30;

    public static AccessKey withExpire(int expireSec){//比起枚举类的好处，可以直接new
        return  new AccessKey(expireSec,"getMiaoshaMostCnt");
    }
}
