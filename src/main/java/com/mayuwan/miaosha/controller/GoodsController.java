package com.mayuwan.miaosha.controller;

import com.alibaba.fastjson.JSON;
import com.mayuwan.miaosha.common.RespBaseVo;
import com.mayuwan.miaosha.common.RespSucVo;
import com.mayuwan.miaosha.common.redis.GoodsKey;
import com.mayuwan.miaosha.common.redis.RedisService;
import com.mayuwan.miaosha.domain.MiaoshaOrder;
import com.mayuwan.miaosha.domain.OrderInfo;
import com.mayuwan.miaosha.domain.User;
import com.mayuwan.miaosha.service.GoodsService;
import com.mayuwan.miaosha.service.OrderService;
import com.mayuwan.miaosha.service.UserService;
import com.mayuwan.miaosha.vo.GoodsDetailVo;
import com.mayuwan.miaosha.vo.GoodsVo;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.spring5.context.webflux.SpringWebFluxContext;
import org.thymeleaf.spring5.view.ThymeleafViewResolver;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;


@Controller
@RequestMapping("goods")
public class GoodsController {
    private static final Logger logger = LoggerFactory.getLogger(GoodsController.class);

    @Autowired
    RedisService redisService;
    @Autowired
    UserService userService;
    @Autowired
    GoodsService goodsService;
    @Autowired
    ThymeleafViewResolver thymeleafViewResolver;


    /**
     *
     * 页面缓存,将Html放到redis中
     * produce与ResponseBody同时存在
     * 注意：ThymeleafViewResolver进行模板渲染
     * 实际应用列表会有分页，只缓存前几页就可以
     * */
    @RequestMapping(value = "to_list",produces ="text/html" )
    @ResponseBody
    public String toList(Model model, HttpServletResponse response, User user,
                        HttpServletRequest request
//                         @CookieValue(value = UserService.COOKIE_NAME_TOKEN,required = false) String loginToken,
//                         @RequestParam(value = UserService.COOKIE_NAME_TOKEN,required = false) String paraToken,
    ){
        //先从缓存中取
        String html =  redisService.get(GoodsKey.LIST_CACHE, "",String.class);
        if(StringUtils.isNotEmpty(html)){
            return html;
        }

        List<GoodsVo> goodsList = goodsService.listGoodsVos();
        model.addAttribute("goodsList",goodsList);
        //手动渲染
        WebContext context = new WebContext(request,response,request.getServletContext(),request.getLocale(),model.asMap());
        html = thymeleafViewResolver.getTemplateEngine().process("goods_list",context);
        //放缓存
        redisService.set(GoodsKey.LIST_CACHE, "",html);
        return html;
    }


    /***
     * ULR缓存
     *
     */
    @RequestMapping(value = "to_detail2/{goodId}",produces = "text/html")
    @ResponseBody
    public String toDetail2(Model model, HttpServletResponse response, User user,HttpServletRequest request,
                           @PathVariable(value = "goodId") Long goodId){
        //先从缓存中取
        String html =  redisService.get(GoodsKey.DETAIL_CACHE, ""+goodId,String.class);//注意key
        if(StringUtils.isNotEmpty(html)){
            return html;
        }

        GoodsVo goods = goodsService.getGoodsVosByGoodId(goodId);
        model.addAttribute("goods",goods);
        model.addAttribute("user",user);

        int miaoshaStatus;
        int remainSeconds;
        Long startDate = goods.getStartDate().getTime();
        Long endDate = goods.getEndDate().getTime();
        Long cur = System.currentTimeMillis();
        if(cur < startDate){//秒杀未开始
            miaoshaStatus = 0;
            remainSeconds = (int)(startDate-cur)/1000;
        } else if (cur > endDate) {//秒杀已结束
            miaoshaStatus = 2;
            remainSeconds = -1;
        }
        else{//秒杀进行中
            miaoshaStatus=1;
            remainSeconds = 0;
        }
        model.addAttribute("miaoshaStatus",miaoshaStatus);
        model.addAttribute("remainSeconds",remainSeconds);

        //手动渲染
        WebContext context = new WebContext(request,response,request.getServletContext(),request.getLocale(),model.asMap());
        html = thymeleafViewResolver.getTemplateEngine().process("goods_detail",context);
        //放缓存
        redisService.set(GoodsKey.DETAIL_CACHE, ""+goodId,html);

        return html;
    }

    /***
     * 纯接口，返回数据
     */
    @RequestMapping(value = "detail/{goodId}",method = RequestMethod.GET)
    @ResponseBody
    public RespBaseVo detail(  User user, @PathVariable(value = "goodId") Long goodId){

        GoodsVo goods = goodsService.getGoodsVosByGoodId(goodId);

        int miaoshaStatus;
        int remainSeconds;
        Long startDate = goods.getStartDate().getTime();
        Long endDate = goods.getEndDate().getTime();
        Long cur = System.currentTimeMillis();
        if(cur < startDate){//秒杀未开始
            miaoshaStatus = 0;
            remainSeconds = (int)(startDate-cur)/1000;
        } else if (cur > endDate) {//秒杀已结束
            miaoshaStatus = 2;
            remainSeconds = -1;
        }
        else{//秒杀进行中
            miaoshaStatus=1;
            remainSeconds = 0;
        }

        GoodsDetailVo goodsDetailVo = new GoodsDetailVo();
        goodsDetailVo.setGoodsVo(goods);
        goodsDetailVo.setMiaoshaStatus(miaoshaStatus);
        goodsDetailVo.setUser(user);
        goodsDetailVo.setRemainSeconds(remainSeconds);

        return RespSucVo.success(goodsDetailVo);
    }
}
