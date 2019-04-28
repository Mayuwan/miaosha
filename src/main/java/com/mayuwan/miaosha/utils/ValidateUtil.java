package com.mayuwan.miaosha.utils;


import org.apache.commons.lang3.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ValidateUtil {

    private static final Pattern MOBILE_PATTERN = Pattern.compile("1\\d{10}");

    public static boolean isMobile(String mobileStr){
        if(StringUtils.isBlank(mobileStr)){return false;}
        Matcher matcher = MOBILE_PATTERN.matcher(mobileStr);
        return matcher.matches();
    }

    public static void main(String[] args) {
        System.out.println(isMobile("18229047585"));
        System.out.println(isMobile("182290475"));
    }
}
