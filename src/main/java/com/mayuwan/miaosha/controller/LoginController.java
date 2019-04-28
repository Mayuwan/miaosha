package com.mayuwan.miaosha.controller;

import com.mayuwan.miaosha.common.RespBaseVo;
import com.mayuwan.miaosha.common.redis.RedisService;
import com.mayuwan.miaosha.domain.User;
import com.mayuwan.miaosha.service.UserService;
import com.mayuwan.miaosha.vo.LoginVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;


@Controller
@RequestMapping("login")
public class LoginController {
    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

    @Autowired
    RedisService redisService;
    @Autowired
    UserService userService;



    @RequestMapping(value = "to_login")
    /**
     *页面输出
     *返回类型String
     * */
    public String toLogin(){
//        logger.info("vo:{}", JSON.toJSONString(loginVo));
        //参数校验
//        if(){}
        //验证手机号格式
        //
        return "login";
    }

    @RequestMapping(value = "do_login",method = RequestMethod.POST)
    @ResponseBody
    public RespBaseVo doLogin(@Valid LoginVo loginVo, HttpServletResponse response,User user){
        if(loginVo == null){
            return RespBaseVo.SERVER_ERROR;
        }
        //参数校验,很麻烦，使用JSR303来进行参数校验，代码简洁且省时间
//        if(StringUtils.isBlank(loginVo.getMobile())){
//            return RespBaseVo.MOBILE_EMPTY;
//        }
//        if(StringUtils.isBlank(loginVo.getPassword())){
//            return RespBaseVo.PASSWORD_EMPTY;
//        }
//        //验证手机号格式
//        if(!ValidateUtil.isMobile(loginVo.getMobile())){
//            return RespBaseVo.MOBILE_FORMAT_ERROR;
//        }

        userService.login(loginVo, response,user);
        return RespBaseVo.SUCCESS;
    }
}
