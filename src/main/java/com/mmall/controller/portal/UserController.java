package com.mmall.controller.portal;

import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;
import com.mmall.service.IUserService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.util.Objects;

/**
 * @author Maoxin
 * @date 1/28/2019
 */
@Controller
@RequestMapping("/user/")
public class UserController {

    @Autowired
    private IUserService iUserService;

    @ResponseBody
    @RequestMapping(value = "login.do",method = RequestMethod.POST)
    public ServerResponse<User> login(String username, String password, HttpSession session){

        ServerResponse<User> response = iUserService.login(username,password);
        if (response.isSuccess()){
            session.setAttribute(Const.CURRENT_USER,response.getData());
        }
        return response;
    }

    @ResponseBody
    @RequestMapping(value = "logout.do",method = RequestMethod.GET)
    public ServerResponse<String> logout(HttpSession session){
        session.removeAttribute(Const.CURRENT_USER);
        return ServerResponse.createBySuccess();
    }
    @ResponseBody
    @RequestMapping(value = "register.do",method = RequestMethod.GET)
    public ServerResponse<String> register(User user){
        return iUserService.register(user);
    }

    @ResponseBody
    @RequestMapping(value = "check_valid.do",method = RequestMethod.GET)
    public ServerResponse<String> checkedValid(String str,String type){
        return iUserService.checkValid(str,type);
    }

    @ResponseBody
    @RequestMapping(value = "get_user_info.do",method = RequestMethod.GET)
    public ServerResponse<User> getUserInfo(HttpSession session){
        return iUserService.getUserInfo(session);
    }

    @ResponseBody
    @RequestMapping(value = "forget_get_question.do",method = RequestMethod.POST)
    public ServerResponse<String> forgetGetQuestion(String username){
        return iUserService.forgetGetAnswer(username);
    }

    @ResponseBody
    @RequestMapping(value = "forget_check_answer.do",method = RequestMethod.GET)
    public ServerResponse<String> forgetCheckAnswer(String username,String question,String answer){
        return iUserService.forgetCheckAnswer(username,question,answer);
    }
    @ResponseBody
    @RequestMapping(value = "forget_reset_password.do",method = RequestMethod.GET)
    public ServerResponse<String> forgetResetPassword(String username,String passwordNew,String forgetToken){
        return iUserService.setNewPassword(username,passwordNew,forgetToken);
    }
    @ResponseBody
    @RequestMapping(value = "reset_password.do",method = RequestMethod.GET)
    public ServerResponse<String> resetPassword(String oldPassword,String newPassword,HttpSession session){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if (Objects.isNull(user)){
            return ServerResponse.createByError("用户未登录");
        }
        return iUserService.resetPassword(oldPassword,newPassword,user);
    }
    @ResponseBody
    @RequestMapping(value = "update_information.do",method = RequestMethod.GET)
    public ServerResponse<String> updateInfomation(HttpSession session,User user){
        User currentUser = (User) session.getAttribute(Const.CURRENT_USER);
        if (Objects.isNull(currentUser)){
            return ServerResponse.createByError("用户未登录");
        }
        return iUserService.updateInfomation(currentUser,user);
    }

    @ResponseBody
    @RequestMapping(value ="get_information.do",method = RequestMethod.GET)
    public ServerResponse<User> getUserInfomation(HttpSession session){
        User currentUser = (User) session.getAttribute(Const.CURRENT_USER);
        if (Objects.isNull(currentUser)){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录，需要强制登录");
        }
        return iUserService.getUserInfomation(currentUser.getId());

    }
}
