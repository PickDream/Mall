package com.mmall.service.impl;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.mmall.common.Const;
import com.mmall.dao.CartMapper;
import com.mmall.dao.ProductMapper;
import com.mmall.pojo.Cart;
import com.mmall.pojo.Product;
import com.mmall.service.ICartService;
import com.mmall.util.BigDecimalUtil;
import com.mmall.util.PropertiesUtil;
import com.mmall.vo.CartProductVo;
import com.mmall.vo.CartVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author Maoxin
 * @ClassName CartServiceImpl
 * @date 2/24/2019
 */
@Service("iCartService")
public class CartServiceImpl implements ICartService {

    @Autowired
    CartMapper cartMapper;
    @Autowired
    ProductMapper productMapper;

    //新增购物车商品
    @Override
    public CartVo addProduct(Integer userId, Integer productId, Integer increaseCount){
        Cart cart = cartMapper.selectByUserIdAndProductId(userId,productId);
        //如果在购物车中不存在该项目
        if (cart==null){
            cart = new Cart();
            cart.setChecked(Const.Cart.CHECKED);
            cart.setProductId(productId);
            cart.setUserId(userId);
            cart.setQuantity(increaseCount);
            cartMapper.insert(cart);
        }else {
            //产品已经在购物车里面了
            increaseCount = cart.getQuantity()+ increaseCount;
            cart.setQuantity(increaseCount);
            cartMapper.updateByPrimaryKey(cart);
        }
        //对商品的下单量进行一个检验，并且确定是否达到最大的购买商品量

        return listAllCart(userId);
    }

    @Override
    public CartVo updateProduct(Integer userId, Integer productId, Integer count){
        Cart cart = cartMapper.selectByUserIdAndProductId(userId,productId);
        if (cart==null) {
            return null;
        }
        cart.setQuantity(count);
        cartMapper.updateByPrimaryKeySelective(cart);
        return listAllCart(userId);
    }

    @Override
    public CartVo deleteProduct(Integer userId, String productIds){
        List<String> productList = Splitter.on(",").splitToList(productIds);
        if (CollectionUtils.isEmpty(productList)){
            return null;
        }
        cartMapper.deleteCartByUSerIdAndProductIds(userId,productList);
        return listAllCart(userId);
    }

    @Override
    public CartVo listCart(Integer userId){
        return listAllCart(userId);
    }


    @Override
    public CartVo selectOrUnSelect(Integer userId, Integer productID, Integer checkedStatus){
        cartMapper.checkedOrUncheckdeProduct(userId,productID,checkedStatus);
        return listAllCart(userId);
    }

    @Override
    public int getCartProductCount(Integer userId){
        if (userId==null){
            return 0;
        }
        return cartMapper.selectCartProductCount(userId);
    }

    /**
     * 组装一个CartProductVo对象
     * @param cart 必须是有id的对象;
     * 在组装的过程中会检查验证与库存之间的关系，并在
     * 必要的时候修该数据库的值
     * */
    private CartProductVo assambleCartProductVo(Cart cart){
        Product product = null;
        product = productMapper.selectByPrimaryKey(cart.getProductId());
        CartProductVo cartProductVo = new CartProductVo();
        cartProductVo.setId(cart.getId());
        cartProductVo.setProductId(cart.getProductId());
        cartProductVo.setProductName(product.getName());
        cartProductVo.setProductSubtitle(product.getSubtitle());
        cartProductVo.setProductMainImage(product.getMainImage());
        cartProductVo.setProductPrice(product.getPrice());
        cartProductVo.setQuantity(cart.getQuantity());
        cartProductVo.setProductStatus(product.getStatus());
        cartProductVo.setProductChecked(cart.getChecked());
        //对于Cart进行限制
        int limitedCount = 0;
        //如果商品的库存大于购物车指明的数量
        if (product.getStock() >= cart.getQuantity()){
            limitedCount = cart.getQuantity();
            cartProductVo.setLimitQuantity(Const.Cart.LIMIT_NUM_SUCCESS);
        }else {
            //商品的库存小于购物车指明的数量
            limitedCount = product.getStock();
            cartProductVo.setQuantity(limitedCount);
            cartProductVo.setLimitQuantity(Const.Cart.LIMIT_NUM_FAIL);
            //修改数据库字段
            Cart cartForQuantity = new Cart();
            cart.setId(cart.getId());
            cart.setQuantity(limitedCount);
            cartMapper.updateByPrimaryKeySelective(cart);
        }
        cartProductVo.setProductTotalPrice(BigDecimalUtil.mul(product.getPrice().doubleValue(),limitedCount));
        return cartProductVo;
    }
    /**
     * 组装一个购物车的视图对象，其中计算的总价只包括了被选中的
     * */
    private CartVo assambleCartVo(List<CartProductVo> cartProducts){
        CartVo cartVo = new CartVo();
        boolean isAllChecked = true;
        BigDecimal totlePrice=new BigDecimal("0");
        for (CartProductVo cpv:cartProducts){
            if (cpv.getProductChecked()==Const.Cart.CHECKED){
                //执行增加BigDecimal的操作
                totlePrice = BigDecimalUtil.add(totlePrice.doubleValue(),cpv.getProductPrice().doubleValue());
            }else{
                isAllChecked = false;
            }
        }
        cartVo.setAllChecked(isAllChecked);
        cartVo.setCartTotalPrice(totlePrice);
        cartVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.perfix","http://47.93.241.68/"));
        return cartVo;
    }
    //列出所有的购物车条目，并且组装成CartVo对象
    private CartVo listAllCart(Integer id){
        List<Cart> cartList = cartMapper.selectCartByUserId(id);
        List<CartProductVo> productVos = Lists.newArrayList();
        for (Cart cart:cartList){
            productVos.add(assambleCartProductVo(cart));
        }
        return assambleCartVo(productVos);
    }

    private boolean getAllChecked(Integer userid){
        if (userid==null){
            return false;
        }
        return cartMapper.selectCartProductCheckedStatusByUserId(userid)==0;
    }
}
