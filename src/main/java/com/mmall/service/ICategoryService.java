package com.mmall.service;

import com.mmall.common.ServerResponse;
import com.mmall.pojo.Category;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;

public interface ICategoryService {

    ServerResponse<String> addCategory(String category, Integer parentId);

    ServerResponse<String> setCategory(Integer categrayId, String categrayName);

    ServerResponse<List<Category>> getChildrenParallelCategory(Integer categoryId);

    ServerResponse<List<Category>> getDeepCategpry(Integer categrayId);
}