package com.spike.controller;

import com.spike.model.MiaoshaUser;
import com.spike.redis.GoodsKey;
import com.spike.redis.RedisService;
import com.spike.result.Result;
import com.spike.service.GoodsService;
import com.spike.service.MiaoshaUserService;
import com.spike.vo.GoodsDetailVo;
import com.spike.vo.GoodsVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.thymeleaf.context.IContext;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.spring5.context.webflux.SpringWebFluxContext;
import org.thymeleaf.spring5.view.ThymeleafViewResolver;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Locale;


@Controller
@RequestMapping("/goods")
public class GoodsController {

	@Autowired
	MiaoshaUserService userService;
	
	@Autowired
	RedisService redisService;

	@Autowired
	GoodsService goodsService;
	@Autowired
	ThymeleafViewResolver thymeleafViewResolver;

	@Autowired
	ApplicationContext applicationContext;
	
    @RequestMapping(value = "/to_list",produces = "text/html")
	@ResponseBody
    public String list(Model model, MiaoshaUser user,
					   HttpServletRequest request, HttpServletResponse response
					   ) {

    	model.addAttribute("user", user);
    	List<GoodsVo> goodsList= goodsService.listGoodsVo();
    	model.addAttribute("goodsList",goodsList);
//        return "goods_list";
		String html = redisService.get(GoodsKey.getGoodsList,"",String.class);
		if(!StringUtils.isEmpty(html)){
			return html;
		}
		//手动渲染
//		SpringWebFluxContext ctx = new SpringWebFluxContext();
		else {
			WebContext ctx = new WebContext(request,response,
					request.getServletContext(),request.getLocale(), model.asMap());
			WebContext context = new WebContext(request,response,request.getServletContext(),
					request.getLocale(),model.asMap());
			 html =thymeleafViewResolver.getTemplateEngine().process("goods_list", context);
			if(!StringUtils.isEmpty(html)) {
				redisService.set(GoodsKey.getGoodsList, "", html);
			}
			return html;
		}

	}


	@RequestMapping(value = "/to_detail/{goodsId}")
	@ResponseBody
	public Result<GoodsDetailVo> detail(MiaoshaUser user,
						 @PathVariable("goodsId")long goodsId
						) {


		GoodsVo goods = goodsService.getGoodsVoByGoodsId(goodsId);

		long startAt = goods.getStartDate().getTime();
		long endAt = goods.getEndDate().getTime();
		long now = System.currentTimeMillis();

		int miaoshaStatus = 0;
		int remainSeconds = 0;
		if (now < startAt) {//秒杀还没开始，倒计时
			miaoshaStatus = 0;
			remainSeconds = (int) ((startAt - now) / 1000);
		} else if (now > endAt) {//秒杀已经结束
			miaoshaStatus = 2;
			remainSeconds = -1;
		} else {//秒杀进行中
			miaoshaStatus = 1;
			remainSeconds = 0;
		}

		GoodsDetailVo goodsDetailVo = new GoodsDetailVo();
		goodsDetailVo.setGoods(goods);
		goodsDetailVo.setMiaoshaStatus(miaoshaStatus);
		goodsDetailVo.setRemainSeconds(remainSeconds);
		goodsDetailVo.setUser(user);
		System.out.println(goodsDetailVo);
		return Result.success(goodsDetailVo);
	}
	@RequestMapping(value = "/to_detail2/{goodsId}",produces = "text/html")
	@ResponseBody
	public String detail2(Model model,MiaoshaUser user,
						 @PathVariable("goodsId")long goodsId,
						 HttpServletRequest request, HttpServletResponse response) {
		model.addAttribute("user", user);

		GoodsVo goods = goodsService.getGoodsVoByGoodsId(goodsId);
		model.addAttribute("goods", goods);

		long startAt = goods.getStartDate().getTime();
		long endAt = goods.getEndDate().getTime();
		long now = System.currentTimeMillis();

		int miaoshaStatus = 0;
		int remainSeconds = 0;
		if (now < startAt) {//秒杀还没开始，倒计时
			miaoshaStatus = 0;
			remainSeconds = (int) ((startAt - now) / 1000);
		} else if (now > endAt) {//秒杀已经结束
			miaoshaStatus = 2;
			remainSeconds = -1;
		} else {//秒杀进行中
			miaoshaStatus = 1;
			remainSeconds = 0;
		}
		model.addAttribute("miaoshaStatus", miaoshaStatus);
		model.addAttribute("remainSeconds", remainSeconds);
//		return "goods_detail";
		String html = redisService.get(GoodsKey.getGoodsDetail,""+goodsId,String.class);
		if(!StringUtils.isEmpty(html)){
			return html;
		}
		//手动渲染
//		SpringWebFluxContext ctx = new SpringWebFluxContext();
		else {
			WebContext context = new WebContext(request,response,request.getServletContext(),
					request.getLocale(),model.asMap());
			html =thymeleafViewResolver.getTemplateEngine().process("goods_detail", context);
			if(!StringUtils.isEmpty(html)){
				redisService.set(GoodsKey.getGoodsDetail,""+goodsId,html);

			}
			return html;
		}
	}
}
