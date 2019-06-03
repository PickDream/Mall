package com.mmall.controller.backend;

import com.github.pagehelper.PageInfo;
import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;
import com.mmall.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.util.Objects;

/**
 * @author Maoxin
 * @date 2/2/2019
 */

@Controller
@RequestMapping("/manage/user")
public class UsermanageController {

    @Autowired
    IUserService iUserService;
    @ResponseBody
    @RequestMapping(value = "login.do")
    public ServerResponse<User> adminLoginIn(String username, String password, HttpSession session){
        ServerResponse<User> response = iUserService.login(username,password);
        if (response.isSuccess()){
            User user = response.getData();
            if (user.getRole()== Const.Role.ROLE_ADMIN){
                session.setAttribute(Const.CURRENT_USER,user);
                return response;
            }else {
                return ServerResponse.createByError("不是管理员,无法登录");
            }
        }
        return response;
    }

    @ResponseBody
    @RequestMapping(value = "list.do")
    public ServerResponse<PageInfo> list(HttpSession session
            , @RequestParam(value = "pageNum",defaultValue = "1")Integer pageNum
            ,@RequestParam(value = "pageSize",defaultValue = "10")Integer pageSize){
            return iUserService.getAllUser(pageNum,pageSize);
    }

}
