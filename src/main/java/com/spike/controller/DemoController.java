package com.spike.controller;


import com.spike.model.User;
import com.spike.redis.RedisService;
import com.spike.redis.UserKey;
import com.spike.result.CodeMsg;
import com.spike.result.Result;
import com.spike.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/demo")
public class DemoController {

    @Autowired
    UserService userService;
    @Autowired
    RedisService redisService;
    @RequestMapping("/hello")
    @ResponseBody
    Result<String> hello(){
        return Result.success("hello");
    }
    @RequestMapping("/helloError")
    @ResponseBody
    Result<String> helloError(){
        return Result.error(CodeMsg.ERROR);
    }
    @RequestMapping("/db/get")
    @ResponseBody
    public Result<User> dbGet(){
        User user = userService.getById(1);
        System.out.println(user);
        return Result.success(user);
    }
    @RequestMapping("/redis/get")
    @ResponseBody
    public Result<User> redisGet(){

        User user= redisService.get(UserKey.getById,""+2,User.class);
        return Result.success(user);
    }
    @RequestMapping("/redis/set")
    @ResponseBody
    public Result<Boolean> redisSet(){
        User user = new User();
        user.setName("1111");
        user.setId(2);
        Boolean ret = redisService.set(UserKey.getById,""+2,user);

        return Result.success(true);
    }
}
