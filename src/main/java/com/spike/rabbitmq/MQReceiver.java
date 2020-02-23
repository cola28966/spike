package com.spike.rabbitmq;

import com.spike.model.MiaoshaOrder;
import com.spike.model.MiaoshaUser;
import com.spike.redis.RedisService;
import com.spike.result.Result;
import com.spike.service.GoodsService;
import com.spike.service.MiaoshaService;
import com.spike.service.OrderService;
import com.spike.vo.GoodsVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MQReceiver {

    private static Logger log = LoggerFactory.getLogger(MQReceiver.class);

    @Autowired
    OrderService orderService;

    @Autowired
    MiaoshaService miaoshaService;

    @Autowired
    GoodsService goodsService;

    @RabbitListener(queues = MQConfig.MIAOShAQUEUE)
    public void receive(String message){
        log.info("receive message:"+message);
        MQMessage mqMessage = RedisService.stringToBean(message,MQMessage.class);
        MiaoshaUser user = mqMessage.getUser();
        long goodsId = mqMessage.getGoodsId();
        //判断是否已经秒杀到了
        MiaoshaOrder order = orderService.getMiaoshaOrderByUserIdGoodsId(user.getId(), goodsId);
        if(order != null) {
            return ;
        }
        GoodsVo goods = goodsService.getGoodsVoByGoodsId(goodsId);
        miaoshaService.miaosha(user,goods);
        //减库存 下订单 写入秒杀订单

    }
}
