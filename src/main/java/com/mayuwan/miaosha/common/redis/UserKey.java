package com.mayuwan.miaosha.common.redis;

public class UserKey extends BaseKeyPrefix {

    private static final int USER_TOKEN_TIME = 3600*24;

    private UserKey(int expireSec, String prefix){
        super(expireSec,prefix);
    }
    private UserKey(String prefix){
        super(prefix);
    }

    public static UserKey getByToken = new UserKey(USER_TOKEN_TIME,"token");
    public static UserKey getById = new UserKey("id");
//    public static OrderKey getByName = new OrderKey(0,"name");
}
