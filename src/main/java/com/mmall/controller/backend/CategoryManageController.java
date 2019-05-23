package com.mmall.controller.backend;

import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.Category;
import com.mmall.pojo.User;
import com.mmall.service.ICategoryService;
import com.mmall.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;

import java.util.List;
import java.util.Objects;


/**
 * @author Maoxin
 * @date 2/2/2019
 */
@Controller
@RequestMapping("/manage/category")
public class CategoryManageController {
    @Autowired
    IUserService iUserService;

    @Autowired
    ICategoryService iCategoryService;

    @ResponseBody
    @RequestMapping(value = "add_category.do",method = RequestMethod.GET)
    public ServerResponse<String> addCategory(
            String categoryName, @RequestParam(defaultValue = "0") int parentId, HttpSession session){
        //校验用户是否登录
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (Objects.isNull(user)){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录，请登录");
        }
        //校验是否是管理员
        if (iUserService.checkAdminRole(user).isSuccess()){
            return iCategoryService.addCategory(categoryName,parentId);
        }

        return ServerResponse.createByError("无权限操作，需要管理员权限");

    }
    @ResponseBody
    @RequestMapping("set_category_name.do")
    public ServerResponse<String> modifyCategory(HttpSession session
            ,@RequestParam(value = "categoryId",defaultValue = "0") Integer categrayId
            ,String categoryName){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (Objects.isNull(user)){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录，请登录");
        }
        if (user.getRole().intValue()==Const.Role.ROLE_ADMIN){
            return iCategoryService.updateCategoryName(categrayId,categoryName);
        }
        return ServerResponse.createByError("无权限操作，需要管理员权限");
    }

    /**
     * 注意，获取类别传入的是品类的父节点，对于根节点是0
     * */
    @ResponseBody
    @RequestMapping("get_category.do")
    public ServerResponse<List<Category>> getChildrenparallelCategory(HttpSession session
            , @RequestParam(value = "categoryId",defaultValue = "0") Integer categoryId){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (Objects.isNull(user)){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录，请登录");
        }
        if (user.getRole().intValue()==Const.Role.ROLE_ADMIN){
            return iCategoryService.getChildrenParallelCategory(categoryId);
        }
        return ServerResponse.createByError("无权限操作，需要管理员权限");
    }

    @ResponseBody
    @RequestMapping("get_deep_category.do")
    public ServerResponse getDeepCategory(HttpSession session
            ,@RequestParam(value = "categoryId",defaultValue = "100001")Integer categoryId){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (Objects.isNull(user)){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录,请登录");
        }
        if (user.getRole().intValue()==Const.Role.ROLE_ADMIN){
            return iCategoryService.selectCategoryAndChildrenById(categoryId);
        }
        return ServerResponse.createByError("无权限操作，需要管理员权限");
    }
}
