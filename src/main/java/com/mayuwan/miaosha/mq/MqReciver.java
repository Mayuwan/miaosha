package com.mayuwan.miaosha.mq;

import com.alibaba.fastjson.JSON;
import com.mayuwan.miaosha.common.RespBaseVo;
import com.mayuwan.miaosha.common.redis.RedisService;
import com.mayuwan.miaosha.domain.MiaoshaOrder;
import com.mayuwan.miaosha.domain.OrderInfo;
import com.mayuwan.miaosha.domain.User;
import com.mayuwan.miaosha.service.GoodsService;
import com.mayuwan.miaosha.service.MiaoshaService;
import com.mayuwan.miaosha.service.OrderService;
import com.mayuwan.miaosha.vo.GoodsVo;
import com.mayuwan.miaosha.vo.MiaoshaMqVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MqReciver {
    private static Logger log = LoggerFactory.getLogger(MqReciver.class);
    @Autowired
    AmqpTemplate amqpTemplate;
    @Autowired
    GoodsService goodsService;
    @Autowired
    OrderService orderService;
    @Autowired
    MiaoshaService miaoshaService;


    @RabbitListener(queues = MqConfig.MIAOSHA_QUEUE)
    public void receive(String message){
        log.info("receive message:"+message);
        MiaoshaMqVo miaoshaMqVo = RedisService.StringToBean(message, MiaoshaMqVo.class);
        Long goodsId = miaoshaMqVo.getGoodsId();
        User user = miaoshaMqVo.getUser();
        //判断库存

        GoodsVo goodsVo = goodsService.getGoodsVosByGoodId(goodsId);
        if(goodsVo ==null ||goodsVo.getStockCount() <= 0){//失败直接返回
           return;
        }
        //判断是否重复下单
        MiaoshaOrder miaoshaOrder = orderService.getMiaoshaOrderByUserIdGoodsId(user.getId(),goodsId);
        if(miaoshaOrder != null){
            return;
        }
        log.info("miaoshaOrder:{}", JSON.toJSONString(miaoshaOrder));
        //减库存，下订单，生成秒杀订单
        OrderInfo orderInfo = miaoshaService.miaosha(user,goodsVo);
        log.info("orderInfo:{}", JSON.toJSONString(orderInfo));
    }

}
