package com.mayuwan.miaosha;

import com.alibaba.fastjson.JSON;
import com.mayuwan.miaosha.common.redis.RedisConfig;
import com.mayuwan.miaosha.dao.MiaoshaOrderMapper;
import com.mayuwan.miaosha.domain.MiaoshaOrder;
import com.mayuwan.miaosha.mq.MqReciver;
import com.mayuwan.miaosha.mq.MqSender;
import com.mayuwan.miaosha.service.GoodsService;
import com.mayuwan.miaosha.service.MiaoshaOrderService;
import com.mayuwan.miaosha.service.OrderService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class MiaoshaApplicationTests {
	@Autowired
	RedisConfig redisConfig;
	@Autowired
	GoodsService goodsService;
	@Autowired
	MqSender mqSender;
	@Autowired
	MqReciver mqReciver;
	@Autowired
	OrderService orderService;
	@Test
	public void contextLoads() {
//		System.out.println(redisConfig.getHost());
	    Long userId = 18229047585L;
	    Long goodId = 1l;
	    Long orderId = 1L;
		System.out.println("miaosha order id:"+Long.valueOf(orderService.insert(userId,goodId,orderId)));
	}

	@Test
	public void remoteDatabase(){
		System.out.println(JSON.toJSONString(goodsService.listGoodsVos()));
	}


	@Test
	public void testMQ(){
		mqSender.sendMessage(goodsService.getGoodsVosByGoodId(1L));

	}

}
