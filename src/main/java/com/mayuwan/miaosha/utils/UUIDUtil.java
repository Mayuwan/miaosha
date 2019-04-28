package com.mayuwan.miaosha.utils;

import java.util.UUID;

public class UUIDUtil {
    public static String getRandomStr(){
        String temp  = UUID.randomUUID().toString().replace("-","");
        return temp.substring(3,3+10);
    }

    public static String uuid() {
        return UUID.randomUUID().toString().replace("-","");
    }
}
