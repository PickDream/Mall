package com.mmall.service;

import com.mmall.common.ServerResponse;
import com.mmall.pojo.Category;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author sly
 * @date 1/28/2019
 */
public interface ICategoryService {
    /**
     * 增加产品类别
     * @param category String
     * @param parentId Integer
     * @return String
     * */
    ServerResponse<String> addCategory(String category, Integer parentId);
    /**
     * 修改产品类别名称
     * @param categoryId Integer
     * @param categoryName String
     * @return String
     * */
    ServerResponse<String> updateCategoryName(Integer categoryId, String categoryName);

    /**
     * 修改产品类别排序优先级
     * @param categoryId Integer
     * @param sortOrder Integer
     * @return String
     * */
    ServerResponse<String> updateCategorySortOrder(Integer categoryId, Integer sortOrder);


    /**
     * 废弃产品类别
     * @param categoryId Integer
     * @return String
     * */
    ServerResponse<String> deprecateCategory(Integer categoryId);


    /**
     * 获取产品类别子节点(平级)
     * @param categoryId Integer
     * @return List<Category>
     */
    ServerResponse<List<Category>> getChildrenParallelCategory(Integer categoryId);

 /**
     * 给其他人提供接口 存排好顺序的
     * @return List<Category>
     */
 ServerResponse<Map<Category,Set<Category>>>  categoriesToOther();


    /**
     * 删除品类以及其之下的所有品类
     * @param categoryId Integer
     * @return String
     */
    ServerResponse<String> deleteCategory(Integer categoryId);

    /**
     * 递归查询本节点的id及孩子节点的id
     * @param categoryId 指明查询节点的Id
     * */
    ServerResponse<List<Integer>> selectCategoryAndChildrenById(Integer categoryId);


}