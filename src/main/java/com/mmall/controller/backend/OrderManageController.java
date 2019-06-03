package com.mmall.controller.backend;

import com.github.pagehelper.PageInfo;
import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;
import com.mmall.service.IOrderService;
import com.mmall.service.IUserService;
import com.mmall.vo.OrderVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.util.Objects;

/**
 * @author Maoxin
 * @ClassName OrderManageController
 * @date 3/19/2019
 */
@Controller
@RequestMapping("/manage/order")
public class OrderManageController {

    @Autowired
    IUserService iUserService;

    @Autowired
    IOrderService iOrderService;


    @ResponseBody
    @RequestMapping("list.do")
    public ServerResponse<PageInfo> orderList(HttpSession session,
                                              @RequestParam(value = "pageNum",defaultValue = "1")int pageNum,
                                              @RequestParam(value = "pageSize",defaultValue = "10")int pageSize){
        return iOrderService.manageList(pageNum,pageSize);
    }

    @ResponseBody
    @RequestMapping("detail.do")
    public ServerResponse<OrderVo> getOrderDetail(HttpSession session,Long orderNo){
        return iOrderService.manageDetail(orderNo);
    }
    @ResponseBody
    @RequestMapping("search.do")
    public ServerResponse searchOrder(HttpSession session,Long orderNo,
                                      @RequestParam(value = "pageNum",defaultValue = "1")int pageNum,
                                      @RequestParam(value = "pageSize",defaultValue = "10")int pageSize){
        return iOrderService.manageSearch(orderNo,pageNum,pageSize);
    }

    @ResponseBody
    @RequestMapping("send_goods.do")
    public ServerResponse<String> orderSendGoods(HttpSession session,Long orderNo){
        return iOrderService.manageSendGoods(orderNo);
    }

}
