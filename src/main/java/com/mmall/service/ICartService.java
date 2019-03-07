package com.mmall.service;

import com.mmall.vo.CartVo;

/**
 * @author Maoxin
 * @ClassName ICartService
 * @date 2/24/2019
 */
public interface ICartService {
    //新增购物车商品
    CartVo addProduct(Integer userId, Integer productId, Integer count);

    CartVo updateProduct(Integer userId, Integer productId, Integer count);

    CartVo deleteProduct(Integer userId, String productIds);

    CartVo listCart(Integer userId);

    /**
     * 对用户购物车列表中的商品的checked属性进行修改
     * 当 @param productID 为null的时候对所有的购物项目进行操作
     * */
    CartVo selectOrUnSelect(Integer userId, Integer productID, Integer checkedStatus);

    int getCartProductCount(Integer userId);
}
