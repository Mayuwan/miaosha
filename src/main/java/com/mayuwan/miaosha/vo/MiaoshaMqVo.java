package com.mayuwan.miaosha.vo;

import com.mayuwan.miaosha.domain.User;

import java.io.Serializable;

public class MiaoshaMqVo implements Serializable {
    private static final long serialVersionUID = -1351242774848900559L;
    private User user;
    private Long goodsId;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Long getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(Long goodsId) {
        this.goodsId = goodsId;
    }
}
