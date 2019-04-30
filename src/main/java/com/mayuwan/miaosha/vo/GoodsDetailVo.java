package com.mayuwan.miaosha.vo;

import com.mayuwan.miaosha.domain.Goods;
import com.mayuwan.miaosha.domain.User;

import java.math.BigDecimal;
import java.util.Date;

public class GoodsDetailVo  {
    private int miaoshaStatus;
    private int remainSeconds;
    private GoodsVo goodsVo;
    private User user;

    public int getMiaoshaStatus() {
        return miaoshaStatus;
    }

    public void setMiaoshaStatus(int miaoshaStatus) {
        this.miaoshaStatus = miaoshaStatus;
    }

    public int getRemainSeconds() {
        return remainSeconds;
    }

    public void setRemainSeconds(int remainSeconds) {
        this.remainSeconds = remainSeconds;
    }

    public GoodsVo getGoodsVo() {
        return goodsVo;
    }

    public void setGoodsVo(GoodsVo goodsVo) {
        this.goodsVo = goodsVo;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
