package com.sky.mapper;

import com.sky.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.Map;

@Mapper
public interface UserMapper {
     Integer countByMap(Map map) ;

    @Select("select  * from user where openid=#{openid}")
    User getByOpenid (String openid);


//    插入数据
    void insert(User user);
    @Select("select  * from user where id=#{id}")
    User getById(Long userId);
}
