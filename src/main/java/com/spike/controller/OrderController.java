package com.spike.controller;

import com.spike.model.MiaoshaUser;
import com.spike.model.OrderInfo;
import com.spike.redis.GoodsKey;
import com.spike.redis.RedisService;
import com.spike.result.CodeMsg;
import com.spike.result.Result;
import com.spike.service.GoodsService;
import com.spike.service.MiaoshaUserService;
import com.spike.service.OrderService;
import com.spike.vo.GoodsDetailVo;
import com.spike.vo.GoodsVo;
import com.spike.vo.OrderDetailVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.spring5.view.ThymeleafViewResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;


@Controller
@RequestMapping("/order")
public class OrderController {

	@Autowired
	MiaoshaUserService userService;
	
	@Autowired
	RedisService redisService;

	@Autowired
	GoodsService goodsService;

	@Autowired
	OrderService orderService;

	
    @RequestMapping(value = "/detail")
	@ResponseBody
   	public Result<OrderDetailVo> info(Model model, MiaoshaUser user,
									  @RequestParam("orderId") long orderId){
    	if(user==null){
    		return Result.error(CodeMsg.SESSION_ERROR);
		}
    	OrderInfo order= orderService.getOrderById(orderId);
		System.out.println(order);
    	if(order==null){
    		return Result.error(CodeMsg.ORDER_NOT_EXIST);
		}
    	long goodsId = order.getGoodsId();
    	GoodsVo goods = goodsService.getGoodsVoByGoodsId(goodsId);
    	OrderDetailVo vo = new OrderDetailVo();
    	vo.setGoods(goods);
    	vo.setOrder(order);
    	return  Result.success(vo);
	}
}
