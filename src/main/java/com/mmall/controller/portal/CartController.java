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
 *
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
    public ServerResponse addProduct(HttpSession session,Integer productId,Integer count){
        if (productId==null|| count ==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.IllEGAL_ARGUMENT.getCode(),"传入参数错误！");
        }
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if (user==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录，请登录");
        }
        CartVo cartVo =  iCartService.addProduct(user.getId(),productId, count);
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

    @ResponseBody
    @RequestMapping("delete_product.do")
    public ServerResponse<CartVo> deleteProduct(HttpSession session,String productIds){
        if (productIds==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.IllEGAL_ARGUMENT.getCode(),"传入参数错误！");
        }
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if (user==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录,请登录");
        }
        CartVo cartVo = iCartService.deleteProduct(user.getId(),productIds);
        return ServerResponse.createBySuccess(cartVo);
    }


    @ResponseBody
    @RequestMapping("list.do")
    public ServerResponse<CartVo> list(HttpSession session){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录,请登录");
        }
        CartVo cartVo = iCartService.listCart(user.getId());
        return ServerResponse.createBySuccess(cartVo);
    }


    //全选
    @ResponseBody
    @RequestMapping("select_all.do")
    public ServerResponse<CartVo> selectAll(HttpSession session){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录,请登录");
        }
        CartVo cartVo = iCartService.selectOrUnSelect(user.getId(),null,Const.Cart.CHECKED);
        return ServerResponse.createBySuccess(cartVo);
    }
    //全反选
    @ResponseBody
    @RequestMapping("un_select_all.do")
    public ServerResponse<CartVo> unSelectAll(HttpSession session){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录,请登录");
        }
        CartVo cartVo = iCartService.selectOrUnSelect(user.getId(),null,Const.Cart.UNCHECKED);
        return ServerResponse.createBySuccess(cartVo);
    }
    //单独选
    @ResponseBody
    @RequestMapping("select.do")
    public ServerResponse<CartVo> select(HttpSession session,Integer productId){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录,请登录");
        }
        CartVo cartVo = iCartService.selectOrUnSelect(user.getId(),productId,Const.Cart.CHECKED);
        return ServerResponse.createBySuccess(cartVo);
    }

    //单独反选
    @ResponseBody
    @RequestMapping("un_select.do")
    public ServerResponse<CartVo> unSelect(HttpSession session,Integer productId){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录,请登录");
        }
        CartVo cartVo = iCartService.selectOrUnSelect(user.getId(),productId,Const.Cart.UNCHECKED);
        return ServerResponse.createBySuccess(cartVo);
    }
    //获取购物车中商品的数量
    @ResponseBody
    @RequestMapping("get_cart_product_count.do")
    public ServerResponse<Integer> getCartProductCount(HttpSession session){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user==null){
            return ServerResponse.createBySuccess(0);
        }
        int count = iCartService.getCartProductCount(user.getId());
        return ServerResponse.createBySuccess(count);
    }

}
