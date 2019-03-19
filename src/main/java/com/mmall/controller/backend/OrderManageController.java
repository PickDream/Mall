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
    @RequestMapping("detail.do")
    public ServerResponse<PageInfo> orderList(HttpSession session,
                                              @RequestParam(value = "pageNum",defaultValue = "1")int pageNum,
                                              @RequestParam(value = "pageSize",defaultValue = "pageSize")int pageSize){
        //校验用户是否登录
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (Objects.isNull(user)){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录，请登录");
        }
        //校验是否是管理员
        if (!iUserService.checkAdminRole(user).isSuccess()){
            return ServerResponse.createByError("无权限操作");
        }
        return iOrderService.manageList(pageNum,pageSize);
    }

    @ResponseBody
    @RequestMapping("detail.do")
    public ServerResponse<OrderVo> getOrderDetail(HttpSession session,Long orderNo){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (Objects.isNull(user)){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录，请登录");
        }
        if (iUserService.checkAdminRole(user).isSuccess()){
            return iOrderService.manageDetail(orderNo);
        }
        return ServerResponse.createByError("无权限操作");

    }
    @ResponseBody
    @RequestMapping("search.do")
    public ServerResponse searchOrder(HttpSession session,Long orderNo,
                                      @RequestParam(value = "pageNum",defaultValue = "1")int pageNum,
                                      @RequestParam(value = "pageSize",defaultValue = "pageSize")int pageSize){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (Objects.isNull(user)){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录，请登录");
        }
        if (iUserService.checkAdminRole(user).isSuccess()){
            return iOrderService.manageSearch(orderNo,pageNum,pageSize);
        }
        return ServerResponse.createByError("无权限操作");
    }

    @ResponseBody
    @RequestMapping("send_goods.do")
    public ServerResponse<String> orderSendGoods(HttpSession session,Long orderNo){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录,请登录");
        }
        if (iUserService.checkAdminRole(user).isSuccess()){
            return iOrderService.manageSendGoods(orderNo);

        }else {
            return ServerResponse.createByError("无权限操作");
        }
    }

}
