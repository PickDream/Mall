package com.mmall.service.impl;

import com.mmall.common.Const;
import com.mmall.dao.CartMapper;
import com.mmall.dao.ProductMapper;
import com.mmall.pojo.Cart;
import com.mmall.pojo.Product;
import com.mmall.service.ICartService;
import com.mmall.vo.CartProductVo;
import com.mmall.vo.CartVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Maoxin
 * @ClassName CartServiceImpl
 * @date 2/24/2019
 */
@Service("ICartService")
public class CartServiceImpl implements ICartService {

    @Autowired
    CartMapper cartMapper;
    @Autowired
    ProductMapper productMapper;

    //新增购物车商品
    public List<Cart> addProduct(Integer userId,Integer productId,Integer count){
        Cart cart = cartMapper.selectByUserIdAndProductId(userId,productId);
        //如果在购物车中不存在该项目
        if (cart==null){
            cart = new Cart();
            cart.setChecked(Const.Cart.CHECKED);
            cart.setProductId(productId);
            cart.setUserId(userId);
            cart.setQuantity(count);
            cartMapper.insert(cart);
        }else {
            //产品已经在购物车里面了
            count = cart.getQuantity()+count;
            cart.setQuantity(count);
        }
        //对商品的下单量进行一个检验，并且确定是否达到最大的购买商品量

        return null;
    }

    //对购物车变动的项目进行检查
    private Cart getLimitedCart(Cart cart){
        int productId = cart.getProductId();
        Product product = productMapper.selectByPrimaryKey(productId);
        int stock = product.getStock();
        if (cart.getQuantity()>stock){
            cart.setQuantity(stock);
        }
        return null;
    }
    //组装一个CartProductVo
    private CartProductVo assambleCartProductVo(Cart cart){
        //
        return null;
    }
    //组装一个Cart4
    private CartVo assambleCartVo(List<CartProductVo> cartProducts){
        return null;
    }
}
