package com.mayuwan.miaosha.service;

import com.mayuwan.miaosha.common.redis.RedisService;
import com.mayuwan.miaosha.dao.GoodsMapper;
import com.mayuwan.miaosha.dao.MiaoshaOrderMapper;
import com.mayuwan.miaosha.dao.OrderInfoMapper;
import com.mayuwan.miaosha.domain.MiaoshaOrder;
import com.mayuwan.miaosha.domain.OrderInfo;
import com.mayuwan.miaosha.domain.User;
import com.mayuwan.miaosha.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
public class OrderService {
    @Autowired
    RedisService redisServic;
    @Autowired
    OrderInfoMapper orderInfoMapper;
    @Autowired
    MiaoshaGoodsService miaoshaGoodsService;
    @Autowired
    MiaoshaOrderService miaoshaOrderService;



    /**
     * 同时向订单表和秒杀订单表里写
     * */


    public OrderInfo insert(Long userId,GoodsVo goodsVo){
        OrderInfo orderInfo = new OrderInfo();
        orderInfo.setCreateDate(new Date());
        orderInfo.setDeliveryAddrId(0L);
        orderInfo.setGoodsCount(1);//商品数量
        orderInfo.setGoodsId(goodsVo.getId());
        orderInfo.setGoodsName(goodsVo.getGoodsName());
        orderInfo.setGoodsPrice(goodsVo.getGoodsPrice());
        orderInfo.setUserId(userId);
        orderInfo.setOrderChannel((byte)0);
        orderInfo.setStatus((byte)0);

        orderInfoMapper.insertSelective(orderInfo);
        return orderInfo;
    }

    @Transactional
    public OrderInfo miaosha(User user, GoodsVo goodsVo) {
        //减少秒杀商品表的库存
        miaoshaGoodsService.reduceStock(goodsVo);
        //订单表增加
        OrderInfo orderInfo = insert(user.getId(),goodsVo);
        //秒杀订单表增加
        miaoshaOrderService.insert(user.getId(),goodsVo.getId(),orderInfo.getId());
        return orderInfo;
    }
}
