package com.mmall.service;

import com.github.pagehelper.PageInfo;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.Shipping;

/**
 * @author Maoxin
 * @date 2/26/2019
 */
public interface IShipingService {

    //添加地址
    ServerResponse add(Integer userId, Shipping shipping);

    ServerResponse delete(Integer userId, Integer shippingId);

    ServerResponse updateShipping(Integer userId, Shipping shipping);

    //查看选中的地址
    ServerResponse select(Integer userId, Integer shippingId);

    //分页获取地址列表
    ServerResponse<PageInfo> list(Integer userId, int pageNumber, int pageSize);
}
