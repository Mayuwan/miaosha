package com.mayuwan.miaosha.controller;

import com.alibaba.fastjson.JSON;
import com.mayuwan.miaosha.common.redis.RedisService;
import com.mayuwan.miaosha.domain.MiaoshaOrder;
import com.mayuwan.miaosha.domain.OrderInfo;
import com.mayuwan.miaosha.domain.User;
import com.mayuwan.miaosha.service.GoodsService;
import com.mayuwan.miaosha.service.OrderService;
import com.mayuwan.miaosha.service.UserService;
import com.mayuwan.miaosha.vo.GoodsVo;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;


@Controller
@RequestMapping("goods")
public class GoodsController {
    private static final Logger logger = LoggerFactory.getLogger(GoodsController.class);

    @Autowired
    RedisService redisService;
    @Autowired
    UserService userService;
    @Autowired
    GoodsService goodsService;


    @RequestMapping(value = "to_list")
    public String toList(Model model, HttpServletResponse response, User user
//                         @CookieValue(value = UserService.COOKIE_NAME_TOKEN,required = false) String loginToken,
//                         @RequestParam(value = UserService.COOKIE_NAME_TOKEN,required = false) String paraToken,
    ){

        List<GoodsVo> goodsList = goodsService.listGoodsVos();
        model.addAttribute("goodsList",goodsList);

        return "goods_list";
    }
    @RequestMapping(value = "to_detail/{goodId}")
    public String toDetail(Model model, HttpServletResponse response, User user,
                           @PathVariable(value = "goodId") Long goodId){

        GoodsVo goods = goodsService.getGoodsVosByGoodId(goodId);
//        logger.info("goods:{}",JSON.toJSONString(goods));
        model.addAttribute("goods",goods);
        model.addAttribute("user",user);


        int miaoshaStatus;
        int remainSeconds;
        Long startDate = goods.getStartDate().getTime();
        Long endDate = goods.getEndDate().getTime();
        Long cur = System.currentTimeMillis();
        if(cur < startDate){//秒杀未开始
            miaoshaStatus = 0;
            remainSeconds = (int)(startDate-cur)/1000;
        } else if (cur > endDate) {//秒杀已结束
            miaoshaStatus = 2;
            remainSeconds = -1;
        }
        else{//秒杀进行中
            miaoshaStatus=1;
            remainSeconds = 0;
        }
        model.addAttribute("miaoshaStatus",miaoshaStatus);
        model.addAttribute("remainSeconds",remainSeconds);
        return "goods_detail";
    }

}
