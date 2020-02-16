package com.spike.service;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import com.spike.dao.MiaoshaUserDao;
import com.spike.exception.GlobalException;
import com.spike.model.MiaoshaUser;
import com.spike.redis.MiaoshaUserKey;
import com.spike.redis.RedisService;
import com.spike.result.CodeMsg;
import com.spike.util.MD5Util;
import com.spike.util.UUIDUtil;
import com.spike.vo.LoginVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;



@Service
public class MiaoshaUserService {
	
	
	public static final String COOKI_NAME_TOKEN = "token";

	
	@Autowired
	MiaoshaUserDao miaoshaUserDao;
	
	@Autowired
	RedisService redisService;
	
	public MiaoshaUser getById(long id) {
		return miaoshaUserDao.getById(id);
	}
	


	

	public boolean login(HttpServletResponse response,LoginVo loginVo) {
		if(loginVo==null){
			throw new GlobalException(CodeMsg.SERVER_ERROR);
		}
		String mobile = loginVo.getMobile();
		String formPass= loginVo.getPassword();
		MiaoshaUser user = getById(Long.parseLong(mobile));
		if(user==null){
			throw new GlobalException(CodeMsg.MOBILE_NOT_EXIST);
		}
		String dbPass = user.getPassword();
		String slatDB = user.getSalt();
		String calcPass = MD5Util.formPassDBPass(formPass,slatDB);
		if(!calcPass.equals(dbPass)){
			throw new GlobalException(CodeMsg.PASSWORD_ERROR);
		}
		String token = UUIDUtil.uuid();
		addCookie(response,user,token);
		return true;
	}


	public MiaoshaUser getByToken(HttpServletResponse response ,String token) {
		if(StringUtils.isEmpty(token)){
			return null;
		}
		//延长有效期
		MiaoshaUser user = redisService.get(MiaoshaUserKey.token,token,MiaoshaUser.class);
		if(user!=null){

			addCookie(response,user,token);
		}
		return user;
	}
	private void addCookie(HttpServletResponse response ,MiaoshaUser user,String token){
		//生成cookie


		redisService.set(MiaoshaUserKey.token,token,user);
		Cookie cookie = new Cookie(COOKI_NAME_TOKEN,token);
		cookie.setMaxAge(MiaoshaUserKey.TOKEN_EXPIRE);
		cookie.setPath("/");
		response.addCookie(cookie);
	}
}
