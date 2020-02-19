package com.spike.dao;

import com.spike.model.MiaoshaOrder;
import com.spike.model.MiaoshaUser;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;



@Mapper
public interface MiaoshaUserDao {
	
	@Select("select * from miaosha_user where id = #{id}")
	public MiaoshaUser getById(@Param("id") long id);

	@Insert("insert into miaosha_user (login_count, nickname, register_date, salt, password, id)" +
			"values(#{loginCount}, #{nickname}, #{registerDate},#{salt},#{password},#{id})")
	public int insertMiaoshaUser(MiaoshaUser miaoshaUser);
}
