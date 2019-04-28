package com.mayuwan.miaosha.service;

import com.mayuwan.miaosha.common.redis.RedisService;
import com.mayuwan.miaosha.dao.MiaoshaOrderMapper;
import com.mayuwan.miaosha.dao.OrderInfoMapper;
import com.mayuwan.miaosha.domain.MiaoshaGoods;
import com.mayuwan.miaosha.domain.MiaoshaOrder;
import com.mayuwan.miaosha.domain.OrderInfo;
import com.mayuwan.miaosha.domain.User;
import com.mayuwan.miaosha.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
public class MiaoshaOrderService {
    @Autowired
    RedisService redisServic;
    @Autowired
    MiaoshaOrderMapper miaoshaOrderMapper;


    public MiaoshaOrder getMiaoshaOrderByUserIdGoodsId(Long userId, Long goodsId) {
        return miaoshaOrderMapper.selectMiaoshaOrderByUserIdGoodsId(userId,goodsId);
    }

    public Long insert(Long userId, Long goodId,Long orderId) {
        MiaoshaOrder miaoshaOrder = new MiaoshaOrder();
        miaoshaOrder.setGoodsId(userId);
        miaoshaOrder.setUserId(goodId);
        miaoshaOrder.setOrderId(orderId);
        miaoshaOrderMapper.insert(miaoshaOrder);
        return miaoshaOrder.getId();
    }



}
