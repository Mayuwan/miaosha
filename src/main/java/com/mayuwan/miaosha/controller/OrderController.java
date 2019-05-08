package com.mayuwan.miaosha.controller;

import com.mayuwan.miaosha.common.RespBaseVo;
import com.mayuwan.miaosha.common.RespSucVo;
import com.mayuwan.miaosha.common.redis.GoodsKey;
import com.mayuwan.miaosha.common.redis.RedisService;
import com.mayuwan.miaosha.domain.Goods;
import com.mayuwan.miaosha.domain.OrderInfo;
import com.mayuwan.miaosha.domain.User;
import com.mayuwan.miaosha.service.GoodsService;
import com.mayuwan.miaosha.service.OrderService;
import com.mayuwan.miaosha.service.UserService;
import com.mayuwan.miaosha.vo.GoodsDetailVo;
import com.mayuwan.miaosha.vo.GoodsVo;
import com.mayuwan.miaosha.vo.OrderDetailVo;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.spring5.view.ThymeleafViewResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;


@Controller
@RequestMapping("order")
public class OrderController {
    private static final Logger logger = LoggerFactory.getLogger(OrderController.class);

    @Autowired
    RedisService redisService;
    @Autowired
    UserService userService;
    @Autowired
    GoodsService goodsService;
    @Autowired
    ThymeleafViewResolver thymeleafViewResolver;
    @Autowired
    OrderService orderService;


    /***
     * 纯接口，返回数据
     */
    @RequestMapping(value = "detail/{orderId}",method = RequestMethod.GET)
    @ResponseBody
    public RespBaseVo detail(  User user, @PathVariable(value = "orderId") Long orderId){

        OrderInfo orderInfo = orderService.getById(orderId);
        if(orderInfo==null) {
            return RespBaseVo.ORDER_NOT_EXIST;
        }
        OrderDetailVo orderDetailVo = new OrderDetailVo();
        orderDetailVo.setOrderInfo(orderInfo);
        orderDetailVo.setGoodsVo(goodsService.getGoodsVosByGoodId(orderInfo.getGoodsId()));
        orderDetailVo.setUser(user);

        return RespSucVo.success(orderDetailVo);
    }
}
