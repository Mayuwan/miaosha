package com.mayuwan.miaosha.dao;

import com.mayuwan.miaosha.domain.Goods;
import com.mayuwan.miaosha.vo.GoodsVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
@Mapper
public interface GoodsMapper {
    int deleteByPrimaryKey(Long id);

    int insert(Goods record);

    int insertSelective(Goods record);

    Goods selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(Goods record);

    int updateByPrimaryKeyWithBLOBs(Goods record);

    int updateByPrimaryKey(Goods record);


    List<GoodsVo> listGoodsVos();

    /**
     * 商品表与秒杀商品表连接查询
     * */
    GoodsVo selectGoodsVosByGoodId(@Param("goodId") Long goodId);
}