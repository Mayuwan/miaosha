package com.mayuwan.miaosha.service;

import com.mayuwan.miaosha.common.RespBaseVo;
import com.mayuwan.miaosha.common.redis.RedisService;
import com.mayuwan.miaosha.common.redis.UserKey;
import com.mayuwan.miaosha.dao.GoodsMapper;
import com.mayuwan.miaosha.dao.UserMapper;
import com.mayuwan.miaosha.domain.User;
import com.mayuwan.miaosha.exceptions.GlobalException;
import com.mayuwan.miaosha.utils.Md5Util;
import com.mayuwan.miaosha.utils.UUIDUtil;
import com.mayuwan.miaosha.vo.GoodsVo;
import com.mayuwan.miaosha.vo.LoginVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Service
public class GoodsService {
    @Autowired
    GoodsMapper goodsMapper;
    @Autowired
    RedisService redisServic;

    public List<GoodsVo> listGoodsVos() {
        return goodsMapper.listGoodsVos();
    }


    public GoodsVo getGoodsVosByGoodId(Long goodId) {
        return goodsMapper.selectGoodsVosByGoodId(goodId);
    }
}
