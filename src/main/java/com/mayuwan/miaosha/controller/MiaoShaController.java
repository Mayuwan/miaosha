package com.mayuwan.miaosha.controller;

import com.mayuwan.miaosha.common.RespBaseVo;
import com.mayuwan.miaosha.common.RespSucVo;
import com.mayuwan.miaosha.common.redis.GoodsKey;
import com.mayuwan.miaosha.common.redis.MiaoshaKey;
import com.mayuwan.miaosha.common.redis.OrderKey;
import com.mayuwan.miaosha.common.redis.RedisService;
import com.mayuwan.miaosha.domain.*;
import com.mayuwan.miaosha.intercepter.AccessFilter;
import com.mayuwan.miaosha.mq.MqSender;
import com.mayuwan.miaosha.service.*;
import com.mayuwan.miaosha.utils.Md5Util;
import com.mayuwan.miaosha.utils.UUIDUtil;
import com.mayuwan.miaosha.vo.GoodsVo;
import com.mayuwan.miaosha.vo.MiaoshaMqVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Controller
@RequestMapping("miaosha")
public class MiaoShaController implements InitializingBean {
    private static final Logger logger = LoggerFactory.getLogger(MiaoShaController.class);

    @Autowired
    RedisService redisService;
    @Autowired
    UserService userService;
    @Autowired
    GoodsService goodsService;
    @Autowired
    OrderService orderService;
    @Autowired
    MqSender mqSender;
    @Autowired
    MiaoshaService miaoshaService;



    private Map<Long,Boolean> goodsIsOver = new HashMap<>();
    /**
     * 系统初始化
     * 预加缓存
     * */
    @Override
    public void afterPropertiesSet() throws Exception {
        List<GoodsVo> goodsVos = goodsService.listGoodsVos();
        if(goodsVos==null || goodsVos.size() <= 0){
            return;
        }
        for (GoodsVo goodsVo : goodsVos) {
            redisService.set(GoodsKey.getStockCount,""+goodsVo.getId(), goodsVo.getStockCount());
            goodsIsOver.put(goodsVo.getId(),false);
        }

    }
    @RequestMapping(value = "reset")
    @ResponseBody
    public RespBaseVo reset( ) {
        List<GoodsVo> goodsVos = goodsService.listGoodsVos();
        for (GoodsVo goodsVo : goodsVos) {
            goodsVo.setStockCount(10);
            redisService.set(GoodsKey.getStockCount,""+goodsVo.getId(), goodsVo.getStockCount());
            goodsIsOver.put(goodsVo.getId(),false);
        }
        redisService.delete(OrderKey.getByUserIdGoodsId);
        redisService.delete(MiaoshaKey.MIAOSHA_STOCK_OVER);
        miaoshaService.reset(goodsVos);
        return RespBaseVo.SUCCESS;
    }

    /**
     * 优化接口
     * */
    @RequestMapping(value = "/{path}/miaosha")
    @ResponseBody
    public RespBaseVo miaosha(User user, @RequestParam("goodsId") Long goodsId,
                              @PathVariable("path") String path) {
        if(user==null){
            return RespBaseVo.NOT_LOGIN;
        }
        //验证验证码
        if(!miaoshaService.checkMiaoshaPath(user.getId(),goodsId,path)){
            return RespBaseVo.REQUEST_ILLEGAL;
        }
        //内存标记
        if(goodsIsOver.get(goodsId)){
            return RespBaseVo.MIAOSHA_OVER;
        }
        //redis预减缓存
        Long remianStock= redisService.decr(GoodsKey.getStockCount,""+goodsId);
//        logger.info("remianStock:{}",remianStock);
//        logger.info("redis key:{}","stockCount"+goodsId);
        if(remianStock < 0){
            goodsIsOver.put(goodsId,true);
            return RespBaseVo.MIAOSHA_OVER;
        }
        //判断是否重复下单
        MiaoshaOrder miaoshaOrder = orderService.getMiaoshaOrderByUserIdGoodsId(user.getId(),goodsId);
        if(miaoshaOrder != null){
            return RespBaseVo.MIAOSHA_REPEAT_ORDER;
        }
        //发送mq
        MiaoshaMqVo miaoshaMqVo = new MiaoshaMqVo();
        miaoshaMqVo.setGoodsId(goodsId);
        miaoshaMqVo.setUser(user);
        mqSender.sendMessage(miaoshaMqVo);
        //客户端进行轮询操作
        return RespBaseVo.SUCCESS; //排队中
    }

    /**
     * 成功：订单id
     * 0：继续轮询
     * -1：失败
     * */
    @RequestMapping(value = "getResult")
    @ResponseBody
    public RespBaseVo getResult(Model model, HttpServletResponse response, User user,
                              @RequestParam("goodsId") Long goodsId) {
        if(user==null){
            return RespBaseVo.NOT_LOGIN;
        }
        long res = miaoshaService.getMiaoshaResult(user.getId(),goodsId);
        logger.info("miaosha result:{}",res);
        return RespSucVo.success(res);
    }

    @AccessFilter(seconds=10,maxCount=5,needLogin=true)
    @RequestMapping(value = "getMiaoshaPath")
    @ResponseBody
    public RespBaseVo getMiaoshaPath(HttpServletResponse response, User user,@RequestParam("verifyCode") Integer verifyCode,
                                @RequestParam("goodsId") Long goodsId) {
        if(user==null){
            return RespBaseVo.NOT_LOGIN;
        }

        //输入的验证码是否争取
        boolean res = miaoshaService.checkVerifyCode(user.getId(),goodsId,verifyCode);
        if(!res){
            return RespBaseVo.VERIFY_CODE;
        }
        String path = miaoshaService.setMiaoshaPath(user.getId(),goodsId);
        return RespSucVo.success(path);
    }

    @RequestMapping(value = "getVerifyCode")
    @ResponseBody
    public RespBaseVo getVerifyCode(HttpServletResponse response, User user,
                                     @RequestParam("goodsId") Long goodsId) {
        if(user==null){
            return RespBaseVo.NOT_LOGIN;
        }
        BufferedImage image = miaoshaService.createVerifyCode(user.getId(),goodsId);
        try{
            ServletOutputStream outputStream = response.getOutputStream();
            ImageIO.write(image,"JPEG",outputStream);
            outputStream.flush();
            outputStream.close();
            return null;
        }catch (Exception e){
            e.printStackTrace();
            return RespBaseVo.SERVER_ERROR;
        }
    }





    @RequestMapping(value = "do_miaosha")
    public String doMiaosha(Model model, HttpServletResponse response, User user,
                            @RequestParam("goodsId") Long goodsId) {
        if(user==null){
            return "login";
        }
        //判断库存
        GoodsVo goodsVo = goodsService.getGoodsVosByGoodId(goodsId);
        if(goodsVo.getStockCount()<=0){
            model.addAttribute("errmsg", RespBaseVo.MIAOSHA_OVER.getMsg());
            return "miaosha_fail";
        }
        //判断是否重复下单
        MiaoshaOrder miaoshaOrder = orderService.getMiaoshaOrderByUserIdGoodsId(user.getId(),goodsVo.getId());
        if(miaoshaOrder != null){
            model.addAttribute("errmsg", RespBaseVo.MIAOSHA_REPEAT_ORDER.getMsg());
            return "miaosha_fail";
        }
        //减库存，下订单，生成秒杀订单
        OrderInfo orderInfo = miaoshaService.miaosha(user,goodsVo);

        model.addAttribute("orderInfo",orderInfo);
        model.addAttribute("goods",goodsVo);
        model.addAttribute("user", user);
        return "order_detail";
    }
}
