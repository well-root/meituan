package com.sky.service;

import com.sky.dto.UserLoginDTO;
import com.sky.entity.User;

public interface UserService {
    //微信登入的功能
    User wxlogin(UserLoginDTO userLoginDTO);
}
