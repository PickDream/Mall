package com.mmall.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
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

import java.util.*;
import java.util.stream.Collectors;

/**
 * 类别管理
 * @author sly
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
        category.setSortOrder(1);
        category.setStatus(true);
        int rowCount =categoryMapper.insert(category);
        if (rowCount>0){
            return ServerResponse.createBySuccess("添加类别成功");
        }
        return ServerResponse.createByError("添加类别信息失败");

    }

    @Override
    public ServerResponse<String> updateCategoryName(Integer categrayId, String categrayName) {
        //注意对参数进行一个判断
        if (categrayId==null||StringUtils.isBlank(categrayName)){
            return ServerResponse.createByError("更新品类参数错误");
        }

        Category category = new Category();
        category.setId(categrayId);
        category.setName(categrayName);
        int rowCount = categoryMapper.updateByPrimaryKeySelective(category);
        if (rowCount>0){
            return ServerResponse.createBySuccess("更新品类名字成功");
        }
        return ServerResponse.createByError("更新品类名字失败");
    }

    @Override
    public ServerResponse<String> updateCategorySortOrder(Integer categoryId, Integer sortOrder) {
        if (categoryId==null||sortOrder==null){
            return ServerResponse.createByError("更新品类参数错误");
        }
        Category category = new Category();
        category.setId(categoryId);
        category.setSortOrder(sortOrder);
        int rowCount = categoryMapper.updateByPrimaryKeySelective(category);
        if (rowCount>0){
            return ServerResponse.createBySuccess("更新品类排序优先级成功");
        }
        return ServerResponse.createByError("更新品类排序优先级失败");
    }

    @Override
    public ServerResponse<String> deprecateCategory(Integer categoryId) {
        if (categoryId==null){
            return ServerResponse.createByError("废弃品类参数错误");
        }
        Category category = new Category();
        List<Integer> ids=Lists.newArrayList();
        getDeepCategoryIds(ids,categoryId);
        int num_row=ids.size();
        int rowCount = 0;
        for (int id:ids){
            category.setId(id);
            category.setStatus(false);
            rowCount += categoryMapper.updateByPrimaryKeySelective(category);
        }
        if (rowCount>=num_row){
            return ServerResponse.createBySuccess("废弃品类成功");
        }


        return ServerResponse.createByError("废弃品类失败");
    }



    @Override
    public ServerResponse<String> deleteCategory(Integer categoryId) {
        if (categoryId==null){
            return ServerResponse.createByError("删除品类参数错误");
        }
        List<Integer> ids=Lists.newArrayList();
        getDeepCategoryIds(ids,categoryId);
        int num_row=ids.size();
        int rowCount = 0;
        for (int id:ids){
            rowCount += categoryMapper.deleteByPrimaryKey(id);
        }
        if (rowCount>=num_row){
            return ServerResponse.createBySuccess("删除品类成功");
        }
        return ServerResponse.createByError("删除品类失败");
    }


    @Override
    public ServerResponse<List<Category>> getChildrenParallelCategory(Integer categoryId){
        //根据ParentId来获取Category的列表
        List<Category> categoryList = categoryMapper.selectCategoryChildrenByParentId(categoryId);
        if (CollectionUtils.isEmpty(categoryList)){
            logger.info("未找到当前分类的子分类");
        }
        return ServerResponse.createBySuccess(categoryList);
    }


    @Override
    public ServerResponse<List<Integer>> selectCategoryAndChildrenById(Integer categoryId) {
        Set<Category> categorySet = Sets.newHashSet();
        findChildCategory(categorySet,categoryId);
        List<Integer> categoryIdList = Lists.newArrayList();
        if(categoryId != null){
            for(Category categoryItem : categorySet){
                categoryIdList.add(categoryItem.getId());
            }
        }
        return ServerResponse.createBySuccess(categoryIdList);
    }

    @Override
    public ServerResponse<Map<Category,Set<Category>>> categoriesToOther() {
        Map<Category,Set<Category>> map= Maps.newHashMap();
        //获取根品类
        List<Category> categoryList = categoryMapper.selectCategoryChildrenByParentId(0);

        categoryList= categoryList.stream().sorted(Comparator.comparing(Category::getSortOrder).reversed()).collect(Collectors.toList());
        for(Category category:categoryList){
            Set<Category> categorySet = Sets.newHashSet();

            findChildCategory(categorySet,category.getId());
            categorySet.remove(category);
            Set<Category> newCategorySet=Sets.newTreeSet((o1, o2) -> {
                //之前如果是0则会损失数据
                if(!o1.getSortOrder().equals(o2.getSortOrder()))
                    return o2.getSortOrder()-o1.getSortOrder();
                else return -1;
            });
            newCategorySet.addAll(categorySet);
            map.put(category,newCategorySet);
        }



        return ServerResponse.createBySuccess(map);
    }


    /**
     * 获取当前品类下的(所有)子品类id的一个list
     */
    private void getDeepCategoryIds(List<Integer> categoryIdList ,Integer categoryId) {
        Set<Category> categoriesSet = Sets.newHashSet();
        findChildCategory(categoriesSet,categoryId);
//        categoryIdList= Lists.newArrayList();
        for (Category categoryItem:categoriesSet){
            categoryIdList.add(categoryItem.getId());
        }
    }

    /**
     * 获取当前品类下的所有子品类
     * */
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
