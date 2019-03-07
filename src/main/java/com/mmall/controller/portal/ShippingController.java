package com.mmall.controller.portal;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.mmall.common.Const;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.Shipping;
import com.mmall.pojo.User;
import com.mmall.service.IShipingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.util.List;

/**
 * @author Maoxin
 * @ClassName ShippingController
 * @date 2/26/2019
 */
@Controller
@RequestMapping("/shipping/")
public class ShippingController {

    @Autowired
    IShipingService iShipingService;

    @ResponseBody
    @RequestMapping("add.do")
    public ServerResponse add(HttpSession session,Shipping shipping){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user==null){
            return ServerResponse.createByError("用户未登录");
        }
        return iShipingService.add(user.getId(),shipping);
    }

    @ResponseBody
    @RequestMapping("del.do")
    public ServerResponse delete(HttpSession session,Integer shippingId){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user==null){
            return ServerResponse.createByError("用户未登录");
        }
        return iShipingService.delete(user.getId(),shippingId);
    }

    @ResponseBody
    @RequestMapping("update.do")
    public ServerResponse update(HttpSession session,Shipping shipping){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user==null){
            return ServerResponse.createByError("用户未登录");
        }
        return iShipingService.updateShipping(user.getId(),shipping);
    }

    @ResponseBody
    @RequestMapping("select.do")
    public ServerResponse select(HttpSession session,Integer shippingId){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user==null){
            return ServerResponse.createByError("用户未登录");
        }
        return iShipingService.select(user.getId(),shippingId);
    }
    @ResponseBody
    @RequestMapping("list.do")
    public ServerResponse<PageInfo> list(@RequestParam(value = "pageNumber",defaultValue = "1")Integer pageNum,
                                  @RequestParam(value = "pageSize",defaultValue = "5")Integer pageSize,
                                  HttpSession session){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user==null){
            return ServerResponse.createByError("未登录");
        }
        return iShipingService.list(user.getId(),pageNum,pageSize);
    }

}
