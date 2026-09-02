package com.sky.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sky.constant.MessageConstant;
import com.sky.dto.UserLoginDTO;
import com.sky.entity.User;
import com.sky.exception.LoginFailedException;
import com.sky.mapper.UserMapper;
import com.sky.properties.WeChatProperties;
import com.sky.utils.HttpClientUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mapping.AccessOptions;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

//微信登入的业务实现那功能
@Service
@Slf4j
public class UserServiceImpl {

    public  static final  String WX_LOGIN="https://api.weixin.qq.com/sns/jscode2session";

    @Autowired
    private WeChatProperties weChatProperties;

    @Autowired
    private UserMapper userMapper;

    public  User wxLogin (UserLoginDTO userLoginDTO){
        String openid= getOpenid(userLoginDTO.getCode());


//        判断是否是新用户
        if(openid==null){
            throw new LoginFailedException(MessageConstant.LOGIN_FAILED);

        }

        User user=userMapper.getByOpenid(openid);

        //新用户自动完成租车

        if(user==null){
            user= User.builder()
                    .openid(openid)
                    .createTime(LocalDateTime.now())
                    .build();
            userMapper.inser(user);


        }
        return user;


    }

    //调用微信接口的服务
    private String getOpenid(String code){
        Map<String,String> map=new HashMap<>();
        map.put("appid",weChatProperties.getAppid());
        map.put("secret",weChatProperties.getSecret());
        map.put("js_code",code);
        map.put("grant_type","authorization_code");
        String json= HttpClientUtil.doGet(WX_LOGIN,map);

        JSONObject jsonObject= JSON.parseObject(json);
        String openid = jsonObject.getString("openid");
        return openid;

    }

}
