package com.mmall.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Maps;
import com.mmall.common.ServerResponse;
import com.mmall.dao.ShippingMapper;
import com.mmall.pojo.Shipping;
import com.mmall.service.IShipingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * 商品地址管理服务
 * @author Maoxin
 * @date 2/26/2019
 */

@Service("iShipingService")
public class ShipingServiceImpl implements IShipingService {

    @Autowired
    ShippingMapper shippingMapper;
    /**
     * 添加地址
     * */
    @Override
    public ServerResponse add(Integer userId, Shipping shipping){
        shipping.setUserId(userId);
        int rowCount = shippingMapper.insert(shipping);
        if (rowCount>0){
            Map<String,Object> result = Maps.newHashMap();
            result.put("shippingId",shipping.getId());
            return ServerResponse.createBySuccess("新建地址成功",result);
        }
        return ServerResponse.createByError("新建地址失败");
    }
    /**
     * 删除地址项
     * 需要注意横向越权漏洞，为防止已登录用户删除不属于
     * 自己的地址信息，需要对传入的shippingId进行验证
     * */
    @Override
    public ServerResponse delete(Integer userId, Integer shippingId){
        int rowCount = shippingMapper.deleteByShippingIdAndUserId(userId,shippingId);
        if (rowCount>0){
            return ServerResponse.createBySuccess("删除地址成功");
        }
        return ServerResponse.createByError("删除地址失败");
    }

    /**
     *对传入的shipping中的信息需要校验，以防止对其修改而造成的横向越权
     * 需要在更新的时候进行校验
     * */

    @Override
    public ServerResponse updateShipping(Integer userId, Shipping shipping){
        //将Session中的Id重新设置到Shipping中
        shipping.setUserId(userId);
        int rowCount = shippingMapper.updateByShipping(shipping);
        if (rowCount>0){
            return ServerResponse.createBySuccess("修改成功");
        }
        return ServerResponse.createByError("修改失败");

    }

    //查看选中的地址
    @Override
    public ServerResponse select(Integer userId, Integer shippingId){
        Shipping shipping = shippingMapper.selectByUserIdAndShippingId(userId,shippingId);
        if (shipping==null){
            return ServerResponse.createByError("无查询结果");
        }
        return ServerResponse.createBySuccess(shipping);
    }

    //分页获取地址列表
    @Override
    public ServerResponse<PageInfo> list(Integer userId,int pageNumber,int pageSize){
        PageHelper.startPage(pageNumber,pageSize);
        List<Shipping> shippingList = shippingMapper.selectAllByUserId(userId);
        PageInfo pageInfo = new PageInfo(shippingList);
        return ServerResponse.createBySuccess(pageInfo);
    }


}
