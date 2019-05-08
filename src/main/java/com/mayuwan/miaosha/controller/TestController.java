package com.mayuwan.miaosha.controller;

import com.alibaba.fastjson.JSON;
import com.mayuwan.miaosha.common.RespBaseVo;
import com.mayuwan.miaosha.common.RespSucVo;
import com.mayuwan.miaosha.common.redis.RedisService;
import com.mayuwan.miaosha.domain.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;


@Controller
@RequestMapping("test")
public class TestController {
    private static final Logger logger = LoggerFactory.getLogger(GoodsController.class);

    @Autowired
    RedisService redisService;


    @RequestMapping("success")
    @ResponseBody
    /**
     * rest api json输出
     *需要ResponseBody注解
     * */
    public RespSucVo success(){
        return RespSucVo.success("all data");
    }

    @RequestMapping("error")
    @ResponseBody
    /**
     * rest api json输出
     *需要ResponseBody注解
     * */
    public RespBaseVo error(){
        return RespBaseVo.SERVER_ERROR;
    }

//    @RequestMapping("getRedis")
//    @ResponseBody
//    public RespSucVo getRedis(){
//        return RespSucVo.success(redisService.get("2",String.class));
//    }
//
    @RequestMapping("getUser")
    @ResponseBody
    public RespSucVo getUser(User user){
        return RespSucVo.success(user);
    }


}
