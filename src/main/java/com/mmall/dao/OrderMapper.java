package com.mmall.dao;

import com.mmall.pojo.Order;
import com.mmall.pojo.OrderItem;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface OrderMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Order record);

    int insertSelective(Order record);

    Order selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Order record);

    int updateByPrimaryKey(Order record);

    Order selectByOrderNoAndUserId(@Param("orderNo") Long orderNo,
                                             @Param("userId") Integer userId);
    Order selectByOrderNo(Long orderNo);

    List<Order> selectByUserId(Integer userId);

    List<Order> selectAllOrder();

    int getOrderTotal();

    /**
     * 需要传入订单状态以及时间来确定带处理的订单
     * */
    List<Order> selectOrderStatusByCreateTime(@Param("status") Integer status,@Param("date")String date);

    int closeOrderByOrderId(Integer orderId);
}

