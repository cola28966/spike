package com.spike.rabbitmq;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;



@Configuration
public class MQConfig {

    public static final String QUEUE = "queue";

    public static final String MIAOShAQUEUE= "miaoshaqueue";
    @Bean
    public Queue queue(){
        return new Queue(QUEUE,true);
    }
    @Bean
    public Queue miaoshaQueue(){
        return new Queue(MIAOShAQUEUE,true);
    }
}
