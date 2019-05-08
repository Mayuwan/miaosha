package com.mayuwan.miaosha.service;

import com.mayuwan.miaosha.common.RespSucVo;
import com.mayuwan.miaosha.common.redis.MiaoshaKey;
import com.mayuwan.miaosha.common.redis.RedisService;
import com.mayuwan.miaosha.dao.GoodsMapper;
import com.mayuwan.miaosha.domain.MiaoshaGoods;
import com.mayuwan.miaosha.domain.MiaoshaOrder;
import com.mayuwan.miaosha.domain.OrderInfo;
import com.mayuwan.miaosha.domain.User;
import com.mayuwan.miaosha.utils.Md5Util;
import com.mayuwan.miaosha.utils.UUIDUtil;
import com.mayuwan.miaosha.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Random;

@Service
public class MiaoshaService {
    @Autowired
    GoodsMapper goodsMapper;
    @Autowired
    RedisService redisService;
    @Autowired
    OrderService orderService;
    @Autowired
    GoodsService goodsService;

    public List<GoodsVo> listGoodsVos() {
        return goodsMapper.listGoodsVos();
    }

    /**
     * 同时向订单表和秒杀订单表里写
     * */
    @Transactional(rollbackFor = Exception.class)
    public OrderInfo miaosha(User user, GoodsVo goodsVo) {
        try{
            //减少秒杀商品表的库存
            boolean res =  goodsService.reduceStock(goodsVo);
            if(!res){
                setMiaoshaStockOver(goodsVo.getId());
                return null;
            }
            //订单表增加，有唯一索引，增加可能会失败
            OrderInfo orderInfo = orderService.insert(user.getId(),goodsVo);
            //秒杀订单表增加
            orderService.insert(user.getId(),goodsVo.getId(),orderInfo.getId());
            return orderInfo;
        }catch (Exception e){
            //代码执行回滚
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return null;
        }
    }
    /**
     * 秒杀商品已经卖完
     * */
    private void setMiaoshaStockOver(Long goodsId) {
        redisService.set(MiaoshaKey.MIAOSHA_STOCK_OVER,""+goodsId,true);
    }


    public long getMiaoshaResult(Long userId, Long goodsId) {
        MiaoshaOrder miaoshaOrder = orderService.getMiaoshaOrderByUserIdGoodsId(userId,goodsId);
        if(miaoshaOrder!=null){
            return miaoshaOrder.getOrderId();
        }
        if(getStockOver(goodsId)){
            return -1;
        }
        else{
            return 0;
        }
    }

    private boolean getStockOver(Long goodsId) {
        return redisService.exists(MiaoshaKey.MIAOSHA_STOCK_OVER, ""+goodsId);
    }

    public void reset(List<GoodsVo> goodsVos) {
        for (GoodsVo goodsVo : goodsVos) {
            MiaoshaGoods g = new MiaoshaGoods();
            g.setGoodsId(goodsVo.getId());
            g.setStockCount(goodsVo.getStockCount());
            goodsService.resetStock(g);
        }
        //删除所有的order
        orderService.deleteAll();
    }

    public String setMiaoshaPath(Long userId, Long goodsId) {
        if(userId == null || goodsId ==null){
            return "";
        }

        String path = UUIDUtil.getRandomStr()+Md5Util.md5(UUIDUtil.uuid());
        redisService.set(MiaoshaKey.getMiaoshaPath,""+userId+"_"+goodsId,path);
        return path;
    }
    public boolean checkMiaoshaPath(Long userId, Long goodsId,String path) {
        String cachePath = redisService.get(MiaoshaKey.getMiaoshaPath,""+userId+"_"+goodsId,String.class);
        return path.equals(cachePath);
    }


//    public static void main(String[] args) {
//        System.out.println(cal("23+34*1"));;
//    }
    private static int cal(String expresion){
        try{
            ScriptEngineManager scriptEngineManager = new ScriptEngineManager();
            ScriptEngine scriptEngine = scriptEngineManager.getEngineByName("javascript");
            return (int)scriptEngine.eval(expresion);
        }catch (Exception e){
            e.printStackTrace();
            return 0;
        }

    }

    private static String getVerifyExpression(Random random){
        int num1 = random.nextInt(10);
        int num2 = random.nextInt(10);
        int num3 = random.nextInt(10);
        char[] operaters = new char[]{'+','-','*'};
        char randomOper = operaters[random.nextInt(operaters.length)];
        return ""+num1+ randomOper + num2 + randomOper + num3;
    }


    public BufferedImage createVerifyCode(Long userId, Long goodsId) {
        if(userId <= 0 || goodsId <=0) {
            return null;
        }
        int width = 80;
        int height = 32;
        //create the image
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics g = image.getGraphics();
        // set the background color
        g.setColor(new Color(0xDCDCDC));
        g.fillRect(0, 0, width, height);
        // draw the border
        g.setColor(Color.black);
        g.drawRect(0, 0, width - 1, height - 1);
        // create a random instance to generate the codes
        Random rdm = new Random();
        // make some confusion
        for (int i = 0; i < 50; i++) {
            int x = rdm.nextInt(width);
            int y = rdm.nextInt(height);
            g.drawOval(x, y, 0, 0);
        }
        // generate a random code
        String verifyCode = getVerifyExpression(rdm);
        g.setColor(new Color(0, 100, 0));
        g.setFont(new Font("Candara", Font.BOLD, 24));
        g.drawString(verifyCode, 8, 24);
        g.dispose();
        //把验证码存到redis中
        int res = cal(verifyCode);
        redisService.set(MiaoshaKey.getMiaoshaVerifyCode, userId+","+goodsId, res);
        //输出图片
        return image;
    }

    public boolean checkVerifyCode(Long userId, Long goodsId, Integer verifyCode) {
        if(verifyCode ==null){
            return false;
        }
        Integer rightCode = redisService.get(MiaoshaKey.getMiaoshaVerifyCode, userId+","+goodsId,Integer.class);
        if(rightCode ==null || rightCode!=verifyCode){
            return false;
        }
        redisService.delete(MiaoshaKey.getMiaoshaVerifyCode, userId+","+goodsId);
        return true;
    }
}
