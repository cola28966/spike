package com.spike.controller;

import com.spike.exception.GlobalException;
import com.spike.model.MiaoshaOrder;
import com.spike.model.MiaoshaUser;
import com.spike.model.OrderInfo;
import com.spike.rabbitmq.MQMessage;
import com.spike.rabbitmq.MQSender;
import com.spike.redis.GoodsKey;
import com.spike.redis.MiaoshaKey;
import com.spike.redis.OrderKey;
import com.spike.redis.RedisService;
import com.spike.result.CodeMsg;
import com.spike.result.Result;
import com.spike.service.GoodsService;
import com.spike.service.MiaoshaService;
import com.spike.service.MiaoshaUserService;
import com.spike.service.OrderService;
import com.spike.vo.GoodsVo;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Controller
@RequestMapping("/miaosha")
public class MiaoshaController implements InitializingBean {

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

	private Map<Long,Boolean> localOverMap = new HashMap<>();

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
		boolean over = localOverMap.get(goodsId);
		if(over){
			return Result.error(CodeMsg.MIAO_SHA_OVER);
		}
		long stock = redisService.decr(GoodsKey.getGoodsStock,""+goodsId);
		if(stock < 0) {
			localOverMap.put(goodsId,true);
			return Result.error(CodeMsg.MIAO_SHA_OVER);
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

	@RequestMapping(value="/reset", method=RequestMethod.GET)
	@ResponseBody
	public Result<Boolean> reset(Model model) {
		List<GoodsVo> goodsList = goodsService.listGoodsVo();
		for(GoodsVo goods : goodsList) {
			goods.setStockCount(10);
			redisService.set(GoodsKey.getGoodsStock, ""+goods.getId(), 10);
			localOverMap.put(goods.getId(), false);
		}
		redisService.delete(OrderKey.getMiaoshaOrderByUidGid);
		redisService.delete(MiaoshaKey.isGoodsOver);
		miaoshaService.reset(goodsList);
		return Result.success(true);
	}
	@Override
	public void afterPropertiesSet() throws Exception {
		List<GoodsVo> goodsVos= goodsService.listGoodsVo();
		for(GoodsVo goods :goodsVos){
			redisService.set(GoodsKey.getGoodsStock,""+goods.getId(),goods.getStockCount());
			localOverMap.put(goods.getId(),false);
		}
	}
}
