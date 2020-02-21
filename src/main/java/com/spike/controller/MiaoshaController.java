package com.spike.controller;

import com.spike.model.MiaoshaOrder;
import com.spike.model.MiaoshaUser;
import com.spike.model.OrderInfo;
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
	OrderService orderService;

	@Autowired
	MiaoshaService miaoshaService;
	//210
    @RequestMapping(value = "/do_miaosha",method = RequestMethod.POST)
	@ResponseBody
    public Result<OrderInfo> list(MiaoshaUser user,
					   @RequestParam("goodsId") long goodsId
					   ) {

    	if(user==null){
    		return Result.error(CodeMsg.SESSION_ERROR);
		}
		//判断库存
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
		return Result.success(orderInfo);

    }


}
