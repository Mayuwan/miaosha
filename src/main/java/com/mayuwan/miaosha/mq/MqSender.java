package com.mayuwan.miaosha.mq;

import com.mayuwan.miaosha.common.redis.RedisService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

@Service
public class MqSender {
    private static Logger log = LoggerFactory.getLogger(MqSender.class);
    @Autowired
    AmqpTemplate amqpTemplate;

    public void sendMessage(Object object){
        String msg =  RedisService.BeanToString(object);
        log.info(msg);
        amqpTemplate.convertAndSend(MqConfig.MIAOSHA_QUEUE,msg);
    }

}
