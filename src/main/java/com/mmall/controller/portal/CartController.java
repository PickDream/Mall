package com.mmall.controller.portal;

import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;
import com.mmall.service.ICartService;
import com.mmall.vo.CartVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

/**
 * @author Maoxin
 * @ClassName CartController
 * @date 2/24/2019
 */

@Controller
@RequestMapping("/cart/")
public class CartController {

    @Autowired
    private ICartService iCartService;

    @ResponseBody
    @RequestMapping("add.do")
    public ServerResponse addProduct(HttpSession session,Integer productId,Integer productCount){
        if (productId==null||productCount==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.IllEGAL_ARGUMENT.getCode(),"传入参数错误！");
        }
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if (user==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录，请登录");
        }
        CartVo cartVo =  iCartService.addProduct(user.getId(),productId,productCount);
        return ServerResponse.createBySuccess(cartVo);
    }

    @ResponseBody
    @RequestMapping("update.do")
    public ServerResponse<CartVo> updateProduct(HttpSession session,Integer productId,Integer count){
        if (productId==null||count==null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.IllEGAL_ARGUMENT.getCode(),"传入参数错误！");
        }
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if (user==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录,请登录");
        }
        CartVo cartVo = iCartService.updateProduct(user.getId(),productId,count);
        return ServerResponse.createBySuccess(cartVo);
    }




}
