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
}
