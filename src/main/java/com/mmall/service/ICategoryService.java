package com.mmall.service;

import com.mmall.common.ServerResponse;
import com.mmall.pojo.Category;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;

public interface ICategoryService {
    /**
     * 增加产品类别
     * @param category String
     * @param parentId Integer
     * @return String
     * */
    ServerResponse<String> addCategory(String category, Integer parentId);
    /**
     * 修改产品类别
     * @param categoryId Integer
     * @param categoryName String
     * @return String
     * */
    ServerResponse<String> setCategory(Integer categoryId, String categoryName);

    /**
     * 获取产品类别子节点(平级)
     * @param categoryId Integer
     * @return List<Category>
     */
    ServerResponse<List<Category>> getChildrenParallelCategory(Integer categoryId);

    /**
     * 获取当前分类id及递归子节点categoryId
     * @param categoryId Integer
     * @return List<Category>
     */
    ServerResponse<List<Category>> getDeepCategory(Integer categoryId);
}