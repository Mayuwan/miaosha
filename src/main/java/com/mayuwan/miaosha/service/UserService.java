package com.mayuwan.miaosha.service;

import com.mayuwan.miaosha.common.RespBaseVo;
import com.mayuwan.miaosha.common.redis.RedisService;
import com.mayuwan.miaosha.common.redis.UserKey;
import com.mayuwan.miaosha.dao.UserMapper;
import com.mayuwan.miaosha.domain.User;
import com.mayuwan.miaosha.exceptions.GlobalException;
import com.mayuwan.miaosha.utils.Md5Util;
import com.mayuwan.miaosha.utils.UUIDUtil;
import com.mayuwan.miaosha.vo.LoginVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

@Service
public class UserService {
    @Autowired
    UserMapper userMapper;
    @Autowired
    RedisService redisServic;


    public static final String COOKIE_NAME_TOKEN = "token";

    public boolean login(LoginVo vo, HttpServletResponse response){
        if(vo == null){
            throw new GlobalException(RespBaseVo.FRONTEND_ERROR);
        }
        String mobile = vo.getMobile();
        String formPass = vo.getPassword();
        //验证手机号是否存在
        User user = getById(Long.valueOf(mobile));
        if(user == null){
            throw new GlobalException(RespBaseVo.MOBILE_UNEXIST);
        }
        //验证密码
        String pass = Md5Util.formPassToDBPass(formPass,user.getSalt());
        if(!user.getPassword().equals(pass)){
            throw new GlobalException( RespBaseVo.PASSWORD_ERROR);
        }
        //生成随机token
        String token = UUIDUtil.uuid();
        //将token放缓存并传给前端
        addCookie(response,user,token);
        //前端页面跳转

        return true;
    }

    private void addCookie(HttpServletResponse response,User user,String token){
        //放缓存
        redisServic.set(UserKey.getByToken,token,user);
        Cookie cookie = new Cookie(COOKIE_NAME_TOKEN,token);
        cookie.setMaxAge(UserKey.getByToken.expireSeconds());
        cookie.setPath("/");
        response.addCookie(cookie);
    }

    public User getByToken(HttpServletResponse response,String token) {
        if (StringUtils.isBlank(token)) {
            return null;
        }
        User user =  redisServic.get(UserKey.getByToken,token,User.class);
        //延长token有效期
        if(user != null){
            addCookie(response,user,token);
        }
        return user;
    }

    /**
     * 先取缓存，缓存取不到取数据库，并放缓存一份
     * */
    public User getById(Long mobile){
        //取缓存
        User user = redisServic.get(UserKey.getById,""+mobile,User.class);
        if(user != null){
            return user;
        }
        user = userMapper.getById(Long.valueOf(mobile));
        if(user != null){
            redisServic.set(UserKey.getById,""+mobile,user);
        }
        return user;
    }


    /**
     *先更新数据库，再更新缓存
     * */
    public boolean updatePassword(String token,Long id,String formPass){
        User user  = getById(id);
        if(user == null){
            throw new GlobalException(RespBaseVo.MOBILE_UNEXIST);
        }
        //更新数据库
        User toBeUpdate = new User();
        toBeUpdate.setId(id);
        toBeUpdate.setPassword(Md5Util.formPassToDBPass(formPass,user.getSalt()));
        userMapper.updateByPrimaryKeySelective(toBeUpdate);
        //更新缓存，需要更新两个地方
        user.setPassword(Md5Util.formPassToDBPass(formPass,user.getSalt()));
        redisServic.set(UserKey.getByToken,token,user);
        redisServic.set(UserKey.getById,""+id,user);
        return true;
    }
}
