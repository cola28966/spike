package com.spike.service;

import com.spike.model.MiaoshaOrder;
import com.spike.model.MiaoshaUser;
import com.spike.model.OrderInfo;
import com.spike.redis.GoodsKey;
import com.spike.redis.MiaoshaKey;
import com.spike.redis.RedisService;
import com.spike.vo.GoodsVo;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
public class MiaoshaService implements InitializingBean {
	
	@Autowired
	GoodsService goodsService;
	
	@Autowired
	OrderService orderService;

	@Autowired
	RedisService redisService;

	@Transactional()
	public OrderInfo miaosha(MiaoshaUser user, GoodsVo goods) {
		//减库存 下订单 写入秒杀订单
		boolean success= goodsService.reduceStock(goods);
		if(success){
			return orderService.createOrder(user,goods);
		}
		else {
			setGoodsOver(goods.getId());
			return null;
		}
		//order_info maiosha_order

	}

	private void setGoodsOver(Long id) {
		redisService.set(MiaoshaKey.isGoodsOver,""+id,true);
	}
	private boolean getGoodsOver(long goodsId) {
		return redisService.exists(MiaoshaKey.isGoodsOver,""+goodsId);
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		List<GoodsVo> goodsVos= goodsService.listGoodsVo();
		for(GoodsVo goods :goodsVos){
			System.out.println(goods.getId());
			redisService.set(GoodsKey.getGoodsStock,""+goods.getId(),goods.getStockCount());
		}
	}

	public long getMiaoShaResult(Long userId, long goodsId) {
		MiaoshaOrder order = orderService.getMiaoshaOrderByUserIdGoodsId(userId,goodsId);
		System.out.println(order);
		if(order!=null){
			return order.getOrderId();
		}else {
			boolean isOver= getGoodsOver(goodsId);
			if(isOver){
				return -1;
			}else {
				return 0;
			}
		}
	}


}
