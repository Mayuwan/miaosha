package com.mayuwan.miaosha.common.redis;

public class MiaoshaKey extends BaseKeyPrefix {

    private MiaoshaKey(int expireSec, String prefix){
        super(expireSec,prefix);
    }
    private MiaoshaKey(String prefix){
        super(prefix);
    }

    private static final int LIST_CACHE_TIME = 30;

    public static MiaoshaKey MIAOSHA_STOCK_OVER = new MiaoshaKey("stockOver");
    public static MiaoshaKey getMiaoshaPath = new MiaoshaKey(60,"getMiaoshaPath");
    public static MiaoshaKey getMiaoshaVerifyCode = new MiaoshaKey(300,"getMiaoshaVerifyCode");
    public static MiaoshaKey getMiaoshaMostCnt = new MiaoshaKey(60,"getMiaoshaMostCnt");
}
