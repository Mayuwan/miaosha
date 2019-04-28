package com.mayuwan.miaosha.utils;

import org.apache.commons.codec.digest.DigestUtils;

public class Md5Util {

    public static String md5(String str){
        return DigestUtils.md5Hex(str);
    }

    private static final String frontSalt = "1a2b3c4d";

    public static String inputPassToFormPass(String inputPass){
        String passPlusSalt = frontSalt.charAt(3)+inputPass+frontSalt.charAt(5)+frontSalt.charAt(1);
        return md5(passPlusSalt);
    }
    public static String formPassToDBPass(String formPass,String salt){
        String temp = salt.charAt(3)+formPass+salt.charAt(5)+formPass+salt.charAt(1);
        return md5(temp);
    }


    public static String inputPassToDBPass(String inputPass,String saltDB){
        return formPassToDBPass(inputPassToFormPass(inputPass),saltDB);
    }

    public static void main(String[] args){
        String input = "123456789";

        System.out.println(inputPassToDBPass(input,"123456"));
    }
}
