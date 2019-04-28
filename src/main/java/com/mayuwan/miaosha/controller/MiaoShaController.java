package com.mayuwan.miaosha.controller;

import com.mayuwan.miaosha.common.RespBaseVo;
import com.mayuwan.miaosha.common.redis.RedisService;
import com.mayuwan.miaosha.domain.*;
import com.mayuwan.miaosha.service.*;
import com.mayuwan.miaosha.vo.GoodsVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletResponse;
import java.util.List;


@Controller
@RequestMapping("miaosha")
public class MiaoShaController {
    private static final Logger logger = LoggerFactory.getLogger(MiaoShaController.class);

    @Autowired
    RedisService redisService;
    @Autowired
    UserService userService;
    @Autowired
    GoodsService goodsService;
    @Autowired
    OrderService orderService;
    @Autowired
    MiaoshaGoodsService miaoshaGoodsService;
    @Autowired
    MiaoshaOrderService miaoshaOrderService;

    @RequestMapping(value = "do_miaosha")
    public String doMiaosha(Model model, HttpServletResponse response, User user,
                            @RequestParam("goodsId") Long goodsId) {
        if(user==null){
            return "login";
        }
        model.addAttribute("user", user);
        //判断库存
        GoodsVo goodsVo = goodsService.getGoodsVosByGoodId(goodsId);
        if(goodsVo.getStockCount()<=0){
            model.addAttribute("errmsg", RespBaseVo.MIAOSHA_OVER.getMsg());
            return "miaosha_fail";
        }
        //判断是否重复下单
        MiaoshaOrder miaoshaOrder = miaoshaOrderService.getMiaoshaOrderByUserIdGoodsId(user.getId(),goodsVo.getId());
        if(miaoshaOrder != null){
            model.addAttribute("errmsg", RespBaseVo.MIAOSHA_REPEAT_ORDER.getMsg());
            return "miaosha_fail";
        }
        //减库存，下订单，生成秒杀订单
        OrderInfo orderInfo = orderService.miaosha(user,goodsVo);
        model.addAttribute("orderInfo",orderInfo);
        model.addAttribute("goods",goodsVo);
        return "order_detail";
    }



}
