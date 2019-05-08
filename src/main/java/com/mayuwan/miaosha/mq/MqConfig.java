package com.mayuwan.miaosha.mq;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MqConfig {

    public static final String  MIAOSHA_QUEUE = "miaosha_queue";
    public static final String  QUEUE = "queue";


    /**
     * Direct模式 交换机Exchange
     * */
    @Bean
    public Queue queue() {
        return new Queue(MIAOSHA_QUEUE, true);
    }
}
