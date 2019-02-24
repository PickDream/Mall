package com.mmall.service;

import com.mmall.common.Const;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;

import javax.servlet.http.HttpSession;

/**
 * @author Maoxin
 * @date 1/28/2019
 */
public interface IUserService {
    ServerResponse<User> login(String username, String password);

    ServerResponse<String> register(User user);

    ServerResponse<String> checkValid(String str,String type);

    ServerResponse<User> getUserInfo(HttpSession session);

    ServerResponse<String> forgetGetAnswer(String username);

    ServerResponse<String> forgetCheckAnswer(String username,String question,String answer);

    ServerResponse<String> setNewPassword(String username,String newPassword,String token);

    ServerResponse<String> resetPassword(String oldPassword,String newPassword,User user);

    ServerResponse<String> updateInfomation(User currentUser, User user);

    ServerResponse<User> getUserInfomation(int id);

    ServerResponse checkAdminRole(User user);
}
