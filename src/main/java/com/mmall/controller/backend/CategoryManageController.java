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

        return iCategoryService.addCategory(categoryName,parentId);
    }

    @ResponseBody
    @RequestMapping("set_category_name.do")
    public ServerResponse<String> modifyCategory(HttpSession session
            ,@RequestParam(value = "categoryId",defaultValue = "0") Integer categrayId
            ,String categoryName){
        return iCategoryService.updateCategoryName(categrayId,categoryName);
    }

    /**
     * 注意，获取类别传入的是品类的父节点，对于根节点是0
     * */
    @ResponseBody
    @RequestMapping("get_category.do")
    public ServerResponse<List<Category>> getChildrenparallelCategory(HttpSession session
            , @RequestParam(value = "categoryId",defaultValue = "0") Integer categoryId){
        return iCategoryService.getChildrenParallelCategory(categoryId);
    }

    @ResponseBody
    @RequestMapping("get_deep_category.do")
    public ServerResponse getDeepCategory(HttpSession session
            ,@RequestParam(value = "categoryId",defaultValue = "100001")Integer categoryId){
        return iCategoryService.selectCategoryAndChildrenById(categoryId);
    }
}
