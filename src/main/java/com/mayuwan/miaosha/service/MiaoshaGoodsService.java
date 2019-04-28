package com.mayuwan.miaosha.service;

import com.mayuwan.miaosha.common.redis.RedisService;
import com.mayuwan.miaosha.dao.GoodsMapper;
import com.mayuwan.miaosha.dao.MiaoshaGoodsMapper;
import com.mayuwan.miaosha.dao.MiaoshaOrderMapper;
import com.mayuwan.miaosha.domain.MiaoshaGoods;
import com.mayuwan.miaosha.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MiaoshaGoodsService {
    @Autowired
    GoodsMapper goodsMapper;
    @Autowired
    RedisService redisServic;
    @Autowired
    MiaoshaGoodsMapper miaoshaGoodsMapper;

    public void reduceStock(GoodsVo goodsVo) {
        MiaoshaGoods miaoshaGoods = new MiaoshaGoods();
        miaoshaGoods.setGoodsId(goodsVo.getId());
        miaoshaGoods.setStockCount(goodsVo.getStockCount()-1);
        miaoshaGoodsMapper.updateByPrimaryKeySelective(miaoshaGoods);
    }
}
