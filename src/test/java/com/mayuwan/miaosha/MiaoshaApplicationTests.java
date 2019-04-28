package com.mayuwan.miaosha;

import com.mayuwan.miaosha.common.redis.RedisConfig;
import com.mayuwan.miaosha.dao.MiaoshaOrderMapper;
import com.mayuwan.miaosha.domain.MiaoshaOrder;
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
	MiaoshaOrderService miaoshaOrderService;

	@Test
	public void contextLoads() {
//		System.out.println(redisConfig.getHost());
	    Long userId = 18229047585L;
	    Long goodId = 1l;
	    Long orderId = 1L;
		System.out.println("miaosha order id:"+Long.valueOf(miaoshaOrderService.insert(userId,goodId,orderId)));
	}

}
