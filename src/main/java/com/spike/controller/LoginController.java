package com.spike.controller;

import com.spike.model.User;
import com.spike.redis.RedisService;
import com.spike.redis.UserKey;
import com.spike.result.CodeMsg;
import com.spike.result.Result;
import com.spike.service.MiaoshaUserService;
import com.spike.service.UserService;
import com.spike.util.ValidatorUtil;
import com.spike.vo.LoginVo;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

@Controller
@RequestMapping("/login")
public class LoginController {

    private static Logger log = LoggerFactory.getLogger(LoginController.class);
    @Autowired
    MiaoshaUserService userService;

    @Autowired
    RedisService redisService;

    @RequestMapping("/to_login")
    public String toLogin(){
        return "login";
    }
    @RequestMapping("/do_login")
    @ResponseBody
    Result<String> doLogin(@Valid LoginVo loginVo, HttpServletResponse response){
         String token=userService.login(response,loginVo);
         return Result.success(token);
    }


}
