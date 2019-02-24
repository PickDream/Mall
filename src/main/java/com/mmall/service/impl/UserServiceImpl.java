package com.mmall.service.impl;

import com.mmall.common.Const;
import com.mmall.common.ServerResponse;
import com.mmall.common.TokenCache;
import com.mmall.dao.UserMapper;
import com.mmall.pojo.User;
import com.mmall.service.IUserService;
import com.mmall.util.MD5Util;
import net.sf.jsqlparser.schema.Server;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.util.Objects;
import java.util.UUID;

/**
 * @author Maoxin
 * @ClassName UserServiceImpl
 * @date 1/28/2019
 */

@Service("iUserService")
public class UserServiceImpl implements IUserService {


    @Autowired
    private UserMapper userMapper;

    @Override
    public ServerResponse<User> login(String username, String password) {

        int resultCount = userMapper.checkUserName(username);
        if (resultCount==0){
            return ServerResponse.createByError("用户名不存在");
        }
        //TODO 密码登录 md5
        String md5Password = MD5Util.MD5EncodeUtf8(password);

        User user = userMapper.selectLogin(username,md5Password);
        if (user==null){
            return ServerResponse.createByError("密码输入错误");
        }
        user.setPassword(StringUtils.EMPTY);
        return ServerResponse.createBySuccess("登录成功",user);
    }

    @Override
    public ServerResponse<String> register(User user) {
        //用户名是否存在
        ServerResponse<String> responseBody = checkValid(user.getUsername(),Const.USER_NAME);
        if (!responseBody.isSuccess()){
            return ServerResponse.createByError("用户名已经存在");
        }
        //校验email是否存在
        responseBody = checkValid(user.getEmail(),Const.EMAIL);
        if (!responseBody.isSuccess()){
            return ServerResponse.createByError("email已经存在");
        }
        user.setRole(Const.Role.ROLE_CUSTOMER);
        user.setPassword(MD5Util.MD5EncodeUtf8(user.getPassword()));

        int resultCode = userMapper.insert(user);
        if (resultCode==0){
            return ServerResponse.createByError("注册失败");
        }
        return ServerResponse.createBySuccessMessage("注册成功");

    }

    @Override
    public ServerResponse<String> checkValid(String str, String type) {
        if (StringUtils.isNotBlank(type)){
            if (Const.USER_NAME.equals(type)){
                int resultCount = userMapper.checkUserName(str);
                if (resultCount>0){
                    return ServerResponse.createByError("已经有该用户");
                }
            }
            if (Const.EMAIL.equals(type)){
                int resultCount = userMapper.checkEmail(str);
                if (resultCount>0){
                    return ServerResponse.createByError("右键已经注册过了");
                }
            }
        }else {
            return ServerResponse.createByError("参数传入错误");
        }

        return ServerResponse.createBySuccess("校验成功");
    }

    @Override
    public ServerResponse<User> getUserInfo(HttpSession session) {
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if (Objects.nonNull(user)){
            return ServerResponse.createBySuccess(user);
        }
        return ServerResponse.createByError("用户未登录,无法获取当前用户信息");
    }

    @Override
    public ServerResponse<String> forgetGetAnswer(String username) {
        ServerResponse<String> validResponse = checkValid(username,Const.USER_NAME);
        if (!validResponse.isSuccess()){
            return ServerResponse.createByError("用户不存在");
        }
        String question = userMapper.selectQuestionByName(username);
        if (StringUtils.isNotBlank(question)){
            return ServerResponse.createBySuccess(question);
        }
        return ServerResponse.createByError("该用户未设置找回密码问题");
    }

    @Override
    public ServerResponse<String> forgetCheckAnswer(String username, String question, String answer) {
        int resultCount = userMapper.checkAnswer(username,question,answer);
        if (resultCount>0){
            String forgetToken = UUID.randomUUID().toString();
            //将其设置到Guava本地缓存当中
            TokenCache.setKey(TokenCache.TOKEN_PREFIX+username,forgetToken);
            ServerResponse.createBySuccess(forgetToken);
        }
        return ServerResponse.createByError("问题答案错误");
    }

    @Override
    public ServerResponse<String> setNewPassword(String username, String newPassword, String token) {
        if (StringUtils.isNotBlank(token)){
            return ServerResponse.createByError("参数错误，token需要传递");
        }
        ServerResponse validResponse = this.checkValid(username,Const.USER_NAME);
        if (!validResponse.isSuccess()){
            return ServerResponse.createByError("用户不存在");
        }
        //验证token
        String savedToken = TokenCache.getKey(TokenCache.TOKEN_PREFIX+username);
        if (StringUtils.isBlank(token)){
            return ServerResponse.createByError("token无效或者已经失效");
        }
        if (StringUtils.equals(savedToken,token)){
            String md5Password = MD5Util.MD5EncodeUtf8(newPassword);
            int rowCount = userMapper.updatePasswordByUsername(username,md5Password);
            if (rowCount>0){
                return ServerResponse.createBySuccess("修改密码成功");
            }
        }else {
            return ServerResponse.createByError("token错误,请重新获取");
        }

        return ServerResponse.createByError("修改密码失败");


    }

    @Override
    public ServerResponse<String> resetPassword(String oldPassword,String newPassword,User user) {
        String md5Password = MD5Util.MD5EncodeUtf8(oldPassword);
        int resultCode = userMapper.checkUserNameAndPassword(user.getUsername(),md5Password);
        if (resultCode>0){
            md5Password = MD5Util.MD5EncodeUtf8(newPassword);
            int effectedRow = userMapper.updatePasswordByUsername(md5Password,user.getUsername());
            if (effectedRow >0){
                return ServerResponse.createBySuccess("密码修改成功");
            }
            return ServerResponse.createByError("密码修改失败");
        }
        return ServerResponse.createByError("旧密码输入错误");
    }

    @Override
    public ServerResponse<String> updateInfomation(User currentUser, User user) {
        //用户名不能被更新！
        user.setUsername(null);
        //email需要校验是否已经存在,不能是其他用户的。
        int resultCount = userMapper.checkEmailNotForUserId(user.getEmail(),user.getId());
        if (resultCount>0){
            return ServerResponse.createByError("当前email已经注册");
        }
        User updateUser = new User();
        updateUser.setId(currentUser.getId());
        updateUser.setEmail(user.getEmail());
        updateUser.setPhone(user.getPhone());
        updateUser.setQuestion(user.getQuestion());
        updateUser.setAnswer(user.getAnswer());

        int effectdRow = userMapper.updateByPrimaryKeySelective(updateUser);
        if (effectdRow>0){
            return ServerResponse.createBySuccess("修改成功");
        }
        return ServerResponse.createByError("修改失败");
    }

    @Override
    public ServerResponse<User> getUserInfomation(int id) {
        User user = userMapper.selectByPrimaryKey(id);
        if (Objects.isNull(user)){
            return ServerResponse.createByError("找不到当前用户");
        }
        user.setPassword(StringUtils.EMPTY);
        return ServerResponse.createBySuccess(user);
    }

    @Override
    public ServerResponse checkAdminRole(User user){
        if (user!=null&&user.getRole().intValue()== Const.Role.ROLE_ADMIN){
            return ServerResponse.createBySuccess();
        }
        return ServerResponse.createByError();
    }
}
