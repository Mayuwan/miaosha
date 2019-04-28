package com.mayuwan.miaosha.dao;

import com.mayuwan.miaosha.domain.MiaoshaOrder;
import com.mayuwan.miaosha.domain.OrderInfo;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OrderInfoMapper {
    int deleteByPrimaryKey(Long id);

    Long insert(OrderInfo record);

    Long insertSelective(OrderInfo record);

    OrderInfo selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(OrderInfo record);

    int updateByPrimaryKey(OrderInfo record);



}