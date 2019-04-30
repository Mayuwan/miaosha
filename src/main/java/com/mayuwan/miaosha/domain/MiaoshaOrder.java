package com.mayuwan.miaosha.domain;

import java.io.Serializable;

public class MiaoshaOrder implements Serializable {
    private static final long serialVersionUID = 8165817847670294848L;
    private Long id;

    private Long userId;

    private Long goodsId;

    private Long orderId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(Long goodsId) {
        this.goodsId = goodsId;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }
}