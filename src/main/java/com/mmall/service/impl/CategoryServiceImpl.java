package com.mmall.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mmall.common.ServerResponse;
import com.mmall.dao.CategoryMapper;
import com.mmall.pojo.Category;
import com.mmall.service.ICategoryService;
import org.apache.commons.lang.StringUtils;
import org.apache.ibatis.annotations.Param;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * @author Maoxin
 * @ClassName CategoryServiceImpl
 * @date 2/2/2019
 */

@Service("iCategoryService")
public class CategoryServiceImpl implements ICategoryService {

    private Logger logger = LoggerFactory.getLogger(CategoryServiceImpl.class);

    @Autowired
    CategoryMapper categoryMapper;

    @Override
    public ServerResponse<String> addCategory(String categoryName, Integer parentId) {
        if (parentId == null|| StringUtils.isBlank(categoryName)){
            return ServerResponse.createByError("添加品类参数错误");
        }
        Category category = new Category();
        category.setName(categoryName);
        category.setParentId(parentId);
        category.setStatus(true);
        int rowCount =categoryMapper.insert(category);
        if (rowCount>0){
            return ServerResponse.createBySuccess("添加类别成功");
        }
        return ServerResponse.createByError("添加类别信息失败");

    }

    @Override
    public ServerResponse<String> setCategory(Integer categrayId, String categrayName) {
        //注意对参数进行一个判断
        if (categrayId==null||StringUtils.isBlank(categrayName)){
            return ServerResponse.createByError("更新品类参数错误");
        }

        Category category = new Category();
        category.setId(categrayId);
        category.setName(categrayName);
        int rowCount = categoryMapper.updateByPrimaryKeySelective(category);
        if (rowCount>0){
            return ServerResponse.createBySuccess("更新品类名字失败");
        }
        return ServerResponse.createByError("更新品类名字成功");
    }

    @Override
    public ServerResponse<List<Category>> getChildrenParallelCategory(Integer categoryId){
        List<Category> categoryList = categoryMapper.selectCategoryChildrenByParentId(categoryId);
        if (CollectionUtils.isEmpty(categoryList)){
            logger.info("未找到当前分类的子分类");
        }
        return ServerResponse.createBySuccess(categoryList);
    }


    @Override
    public ServerResponse getDeepCategpry(Integer categoryId) {
        Set<Category> categorieSet = Sets.newHashSet();
        findChildCategory(categorieSet,categoryId);
        List<Integer> categoryIdList = Lists.newArrayList();
        for (Category categoryItem:categorieSet){
            categoryIdList.add(categoryItem.getId());
        }
        return ServerResponse.createBySuccess(categoryIdList);

    }

    private void findChildCategory(Set<Category> categories,Integer categoryId){
        Category category = categoryMapper.selectByPrimaryKey(categoryId);
        if (Objects.isNull(category)){
            return;
        }
        //添加当前元素到Set中
        categories.add(category);
        List<Category> lists = categoryMapper.selectCategoryChildrenByParentId(categoryId);
        for (Category c:lists){
            findChildCategory(categories,c.getId());
        }
    }
}
