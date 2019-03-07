package com.mmall.dao;

import com.mmall.pojo.Cart;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface CartMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Cart record);

    int insertSelective(Cart record);

    Cart selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Cart record);

    int updateByPrimaryKey(Cart record);

    Cart selectByUserIdAndProductId(@Param("userId") Integer userId,@Param("productId") Integer productId);

    List<Cart> selectCartByUserId(int userid);

    int selectCartProductCheckedStatusByUserId(int userid);

    int deleteCartByUSerIdAndProductIds(@Param("userid") int userid,@Param("productIdList") List<String> prodductIds);

    int checkedOrUncheckdeProduct(@Param("userid") Integer userId,@Param("productId") Integer productId,@Param("checked") Integer checked);

    int selectCartProductCount(@Param("userid")Integer userId);
}