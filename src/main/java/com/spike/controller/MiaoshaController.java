package com.spike.controller;

import com.spike.exception.GlobalException;
import com.spike.model.MiaoshaOrder;
import com.spike.model.MiaoshaUser;
import com.spike.model.OrderInfo;
import com.spike.rabbitmq.MQMessage;
import com.spike.rabbitmq.MQSender;
import com.spike.redis.GoodsKey;
import com.spike.redis.RedisService;
import com.spike.result.CodeMsg;
import com.spike.result.Result;
import com.spike.service.GoodsService;
import com.spike.service.MiaoshaService;
import com.spike.service.MiaoshaUserService;
import com.spike.service.OrderService;
import com.spike.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Controller
@RequestMapping("/miaosha")
public class MiaoshaController {

	@Autowired
	MiaoshaUserService userService;
	
	@Autowired
	RedisService redisService;

	@Autowired
	GoodsService goodsService;

	@Autowired
	MQSender mqSender;

	@Autowired
	OrderService orderService;

	@Autowired
	MiaoshaService miaoshaService;
	//210
    @RequestMapping(value = "/do_miaosha",method = RequestMethod.POST)
	@ResponseBody
    public Result<Integer> list(MiaoshaUser user,
					   @RequestParam("goodsId") long goodsId
					   ) {

    	if(user==null){
    		return Result.error(CodeMsg.SESSION_ERROR);
		}
	/*	//判断库存
		GoodsVo goods = goodsService.getGoodsVoByGoodsId(goodsId);
		int stock = goods.getStockCount();
		if(stock <= 0) {
			return Result.error(CodeMsg.MIAO_SHA_OVER);
		}
		//判断是否已经秒杀到了
		MiaoshaOrder order = orderService.getMiaoshaOrderByUserIdGoodsId(user.getId(), goodsId);
		if(order != null) {
			return Result.error(CodeMsg.REPEATE_MIAOSHA);
		}
		//减库存 下订单 写入秒杀订单
		OrderInfo orderInfo = miaoshaService.miaosha(user, goods);
		return Result.success(orderInfo);*/
		long stock = redisService.decr(GoodsKey.getGoodsStock,""+goodsId);
		if(stock < 0) {
			return Result.success(-1);
		}

		MQMessage message = new MQMessage();
		message.setGoodsId(goodsId);
		message.setUser(user);
		mqSender.send(message);
		return Result.success(0);
    }

	@RequestMapping(value = "/result",method = RequestMethod.GET)
	@ResponseBody
	public Result<Long> getResult(MiaoshaUser user,
								@RequestParam("goodsId") long goodsId
	) {

		if(user==null){
			return Result.error(CodeMsg.SESSION_ERROR);
		}
		long result = miaoshaService.getMiaoShaResult(user.getId(), goodsId);
		return Result.success(result);
    }


}
