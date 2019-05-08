package com.mayuwan.miaosha.intercepter;

import com.mayuwan.miaosha.domain.User;

public class UserContext {
    private static ThreadLocal<User> local = new ThreadLocal<>();

    public static User get(){
        return local.get();
    }
    public static void set(User user){
        local.set(user);
    }
}
