package com.mayuwan.miaosha.dao;

import com.mayuwan.miaosha.domain.MiaoshaGoods;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MiaoshaGoodsMapper {
    int deleteByPrimaryKey(Long id);

    int insert(MiaoshaGoods record);

    int insertSelective(MiaoshaGoods record);

    MiaoshaGoods selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(MiaoshaGoods record);

    int updateByPrimaryKey(MiaoshaGoods record);
}