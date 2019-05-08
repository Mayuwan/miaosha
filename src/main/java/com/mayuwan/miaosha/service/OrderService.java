package com.mayuwan.miaosha.service;

import com.mayuwan.miaosha.common.redis.MiaoshaKey;
import com.mayuwan.miaosha.common.redis.OrderKey;
import com.mayuwan.miaosha.common.redis.RedisService;
import com.mayuwan.miaosha.dao.MiaoshaOrderMapper;
import com.mayuwan.miaosha.dao.OrderInfoMapper;
import com.mayuwan.miaosha.domain.MiaoshaOrder;
import com.mayuwan.miaosha.domain.OrderInfo;
import com.mayuwan.miaosha.domain.User;
import com.mayuwan.miaosha.vo.GoodsVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.util.Date;

@Service
public class OrderService {
    private static final Logger logger = LoggerFactory.getLogger(OrderService.class);

    @Autowired
    RedisService redisServic;
    @Autowired
    OrderInfoMapper orderInfoMapper;
    @Autowired
    GoodsService goodsService;
    @Autowired
    OrderService orderService;
    @Autowired
    MiaoshaOrderMapper miaoshaOrderMapper;



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


    public OrderInfo getById(Long id){
        return orderInfoMapper.selectByPrimaryKey(id);
    }




    /**加缓存
     * */
    public MiaoshaOrder getMiaoshaOrderByUserIdGoodsId(Long userId, Long goodsId) {
        MiaoshaOrder miaoshaOrder = redisServic.get(OrderKey.getByUserIdGoodsId,""+userId+"_"+goodsId,MiaoshaOrder.class);
        if (miaoshaOrder!=null) {
            return miaoshaOrder;
        }
        miaoshaOrder = miaoshaOrderMapper.selectMiaoshaOrderByUserIdGoodsId(userId,goodsId);
        if(miaoshaOrder!=null){
            redisServic.set(OrderKey.getByUserIdGoodsId,""+userId+"_"+goodsId,miaoshaOrder);
        }
        return miaoshaOrder;
    }

    public Long insert(Long userId, Long goodId,Long orderId) {
        MiaoshaOrder miaoshaOrder = new MiaoshaOrder();
        miaoshaOrder.setGoodsId(goodId);
        miaoshaOrder.setUserId(userId);
        miaoshaOrder.setOrderId(orderId);
        miaoshaOrderMapper.insert(miaoshaOrder);
        return miaoshaOrder.getId();
    }

    public void deleteAll() {
        miaoshaOrderMapper.deleteAll();
        orderInfoMapper.deleteAll();
    }
}
