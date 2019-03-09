package com.mmall.service;

import com.mmall.common.ServerResponse;

import java.util.Map;

public interface IOrderService {

    ServerResponse createOrder(Integer userId, Integer shippingId);

    ServerResponse pay(Long orderNo, Integer userId, String path);

    ServerResponse checkOrderInfo(Map<String, String> params);

    ServerResponse queryOrderStatus(Integer userId,Long orderNo);
}
