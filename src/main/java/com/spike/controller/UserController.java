package com.spike.controller;

import com.spike.model.MiaoshaUser;
import com.spike.model.User;
import com.spike.redis.RedisService;
import com.spike.result.Result;
import com.spike.service.GoodsService;
import com.spike.service.MiaoshaUserService;
import com.spike.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;


@Controller
@RequestMapping("/user")
public class UserController {

	@Autowired
	MiaoshaUserService userService;
	
	@Autowired
	RedisService redisService;

	@Autowired
	GoodsService goodsService;
	
    @RequestMapping("/info")
	@ResponseBody
    public Result<MiaoshaUser> info(Model model, MiaoshaUser user
					   ) {
        return Result.success(user);
    }
}
