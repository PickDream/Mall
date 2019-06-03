package com.mmall.controller.backend;

import com.google.common.collect.Maps;
import com.mmall.common.ServerResponse;
import com.mmall.dao.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/manage/monitor")
public class MonitorController {

    @Autowired
    CartMapper cartMapper;
    @Autowired
    CategoryMapper categoryMapper;
    @Autowired
    OrderMapper orderMapper;
    @Autowired
    ProductMapper productMapper;
    @Autowired
    UserMapper userMapper;

    @ResponseBody
    @RequestMapping("server_status.do")
    public ServerResponse getServerStatus(){
        Runtime runtime = Runtime.getRuntime();
        Map<String,String> props = new HashMap<>();
        props.put("jvmTotalMemory",runtime.totalMemory()/1024 + "M");
        props.put("jvmMemoryRatio",(runtime.totalMemory()-runtime.freeMemory())*100/runtime.totalMemory() +"%");
        return ServerResponse.createBySuccess(props);
    }

    @ResponseBody
    @RequestMapping("table_count.do")
    public ServerResponse getTableCount(){
        Map<String,Number> countMap = Maps.newHashMap();
        countMap.put("订单总数",orderMapper.getOrderTotal());
        countMap.put("用户总数",userMapper.getUserTotal());
        countMap.put("商品总数",productMapper.getProductTotal());
        countMap.put("品类总数",categoryMapper.getCategoryTotal());
        return ServerResponse.createBySuccess(countMap);
    }
}
