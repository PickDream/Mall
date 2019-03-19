package com.mmall.service.impl;

import com.alipay.api.response.AlipayTradePrecreateResponse;
import com.alipay.demo.trade.config.Configs;
import com.alipay.demo.trade.model.GoodsDetail;
import com.alipay.demo.trade.model.builder.AlipayTradePrecreateRequestBuilder;
import com.alipay.demo.trade.model.result.AlipayF2FPrecreateResult;
import com.alipay.demo.trade.service.AlipayTradeService;
import com.alipay.demo.trade.service.impl.AlipayTradeServiceImpl;
import com.alipay.demo.trade.utils.ZxingUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mmall.common.Const;
import com.mmall.common.ServerResponse;
import com.mmall.dao.*;
import com.mmall.pojo.*;
import com.mmall.service.IOrderService;
import com.mmall.util.BigDecimalUtil;
import com.mmall.util.DateTimeUtil;
import com.mmall.util.FtpUtil;
import com.mmall.util.PropertiesUtil;
import com.mmall.vo.OrderItemVo;
import com.mmall.vo.OrderProductVo;
import com.mmall.vo.OrderVo;
import com.mmall.vo.ShippingVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.File;
import java.math.BigDecimal;
import java.util.*;

/**
 * @author Maoxin
 * @ClassName OrderServiceImpl
 * @date 3/2/2019
 */

@Service("iOrderService")
public class OrderServiceImpl implements IOrderService {

    private static final Logger logger = LoggerFactory.getLogger(OrderServiceImpl.class);
    @Autowired
    OrderMapper orderMapper;
    @Autowired
    OrderItemMapper orderItemMapper;
    @Autowired
    PayInfoMapper payInfoMapper;
    @Autowired
    CartMapper cartMapper;
    @Autowired
    ProductMapper productMapper;
    @Autowired
    ShippingMapper shippingMapper;

    @Override
    public ServerResponse createOrder(Integer userId, Integer shippingId){
        //获取所有的购物车被选中的项目
        List<Cart> carts = cartMapper.selectCheckedCartByUserId(userId);
        //生成对饮的订单细节项目
        ServerResponse<List<OrderItem>> serverResponse = getCartOrderItem(userId,carts);
        if (!serverResponse.isSuccess()){
            return serverResponse;
        }

        List<OrderItem> orderItemList = serverResponse.getData();
        BigDecimal payment = getOrderTotalPrice(orderItemList);

        //生成一个订单
        Order order = assambleOrder(userId,shippingId,payment);
        if (Objects.isNull(order)){
            return ServerResponse.createByError("生成订单错误");
        }
        if (CollectionUtils.isEmpty(orderItemList)){
            return ServerResponse.createByError("购物车为空");
        }
        //首先完善订单item的信息
        for (OrderItem orderItem:orderItemList){
            orderItem.setOrderNo(order.getOrderNo());
        }
        //使用mybatis进行批量的插入
        orderItemMapper.batchInsert(orderItemList);
        //清空购物车
        cleanCart(carts);
        //减少库存
        reduceProductStock(orderItemList);
        //给前端返回数据
        OrderVo orderVo = assambleOrderVo(order,orderItemList);
        return ServerResponse.createBySuccess(orderVo);


    }

    private OrderVo assambleOrderVo(Order order,List<OrderItem> orderItemList){
        OrderVo orderVo = new OrderVo();
        orderVo.setOrderNo(order.getOrderNo());
        orderVo.setPayment(order.getPayment());
        orderVo.setPaymentType(order.getPaymentType());
        orderVo.setPaymentTypeDesc(Const.PaymentTypeEnum.codeOf(order.getPaymentType()).getValue());

        orderVo.setPostage(order.getPostage());
        orderVo.setStatus(order.getStatus());
        orderVo.setStatusDesc(Const.OrderStatusEnum.codeOf(order.getStatus()).getValue());

        orderVo.setShippingId(order.getShippingId());
        Shipping shipping =shippingMapper.selectByPrimaryKey(order.getShippingId());
        if (shipping!=null){
            orderVo.setRecevierName(shipping.getReceiverName());
            orderVo.setShippingVo(assambleShippingVo(shipping));
        }
        orderVo.setPaymentTime(DateTimeUtil.dateToStr(order.getPaymentTime()));
        orderVo.setSendTime(DateTimeUtil.dateToStr(order.getSendTime()));
        orderVo.setEndTime(DateTimeUtil.dateToStr(order.getEndTime()));
        orderVo.setCreateTime(DateTimeUtil.dateToStr(order.getCreateTime()));
        orderVo.setCloseTime(DateTimeUtil.dateToStr(order.getCloseTime()));

        orderVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix",""));
        //组装OrderItemVo
        List<OrderItemVo> orderItemVoList = Lists.newArrayList();
        for (OrderItem orderItem:orderItemList){
            OrderItemVo orderItemVo = assambleOrderItemVo(orderItem);
            orderItemVoList.add(orderItemVo);
        }
        return orderVo;
    }

    private OrderItemVo assambleOrderItemVo(OrderItem orderItem){
        OrderItemVo orderItemVo = new OrderItemVo();
        orderItemVo.setOrderNo(orderItem.getOrderNo());
        orderItemVo.setProductId(orderItem.getProductId());
        orderItemVo.setProductName(orderItem.getProductName());
        orderItemVo.setProductImage(orderItem.getProductImage());
        orderItemVo.setCurrentUnitPrice(orderItem.getCurrentUnitPrice());
        orderItemVo.setTotalPrice(orderItem.getTotalPrice());
        orderItemVo.setCreateTime(DateTimeUtil.dateToStr(orderItem.getCreateTime()));
        return orderItemVo;
    }
    /**
     * 组装购物车的ValueObject
     * */
    private ShippingVo assambleShippingVo(Shipping shipping){
        ShippingVo shippingVo = new ShippingVo();
        shippingVo.setReceiverName(shipping.getReceiverName());
        shippingVo.setReceiverAddress(shipping.getReceiverAddress());
        shippingVo.setReceiverProvince(shipping.getReceiverProvince());
        shippingVo.setReceiverCity(shipping.getReceiverCity());
        shippingVo.setReceiverDistrict(shipping.getReceiverDistrict());
        shippingVo.setReceiverMobile(shipping.getReceiverMobile());
        shippingVo.setReceiverZip(shipping.getReceiverZip());
        shippingVo.setReceiverPhone(shippingVo.getReceiverPhone());
        return shippingVo;
    }
    private void cleanCart(List<Cart> cartList){
        for (Cart cart:cartList){
            cartMapper.deleteByPrimaryKey(cart.getId());
        }
    }
    /**
     * 减少库存
     * */
    private void reduceProductStock(List<OrderItem> orderItemList){
        for (OrderItem orderItem:orderItemList){
            Product product = productMapper.selectByPrimaryKey(orderItem.getProductId());
            Product nProduct = new Product();
            nProduct.setId(product.getId());
            nProduct.setStock(product.getStock()-orderItem.getQuantity());
            productMapper.updateByPrimaryKeySelective(nProduct);
        }
    }
    /**
     * 生成订单号
     * 同一时间下单依旧可能会出现问题
     * */
    private long generateOrderNo(){
        long currentTime = System.currentTimeMillis();
        return currentTime+new Random().nextInt(100);
    }
    private BigDecimal getOrderTotalPrice(List<OrderItem> orderItemList){
        BigDecimal payment = new BigDecimal("0");
        for (OrderItem orderItem:orderItemList){
            payment = BigDecimalUtil.add(payment.doubleValue(),orderItem.getTotalPrice().doubleValue());
        }
        return payment;
    }
    /**
     *根据购物车信息获取OrderItem的List
     * */
    public ServerResponse<List<OrderItem>> getCartOrderItem(Integer userId,List<Cart> cartList){
        List<OrderItem> orderItemList = Lists.newArrayList();
        if (CollectionUtils.isEmpty(cartList)){
            return ServerResponse.createByError("购物车为空");
        }
        for (Cart cartItem:cartList){
            OrderItem orderItem = new OrderItem();
            Product product = productMapper.selectByPrimaryKey(cartItem.getProductId());
            if (Const.ProductStatusEnum.ON_SALE.getCode()!=product.getStatus()){
                return ServerResponse.createByError("产品"+product.getName()+"当前不是在售状态");
            }
            //再校验库存
            if (cartItem.getQuantity()>product.getStock()){
                return ServerResponse.createByError("产品"+product.getName()+"库存不足");
            }
            //对订单信息做快照处理
            orderItem.setUserId(userId);
            orderItem.setProductId(product.getId());
            orderItem.setCurrentUnitPrice(product.getPrice());
            orderItem.setProductImage(product.getMainImage());
            orderItem.setProductName(product.getName());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setTotalPrice(BigDecimalUtil.mul(product.getPrice().doubleValue(),cartItem.getQuantity()));
            orderItemList.add(orderItem);
        }
        return ServerResponse.createBySuccess(orderItemList);
    }

    /**
     * 根据基本信息组装Order对象
     * */
    private Order assambleOrder(Integer userId,Integer shippingId,BigDecimal payment){
        Order order = new Order();
        long orderNo = this.generateOrderNo();
        order.setUserId(userId);
        order.setShippingId(shippingId);
        order.setPayment(payment);
        order.setStatus(Const.OrderStatusEnum.NOT_PAY.getCode());
        order.setPostage(0);
        order.setOrderNo(orderNo);
        order.setPaymentType(Const.PaymentTypeEnum.ONLINE_PAY.getCode());
        int rowCount = orderMapper.insert(order);
        if (rowCount>0){
            return order;
        }
        return null;
    }

    @Override
    public ServerResponse pay(Long orderNo,Integer userId,String path){

        Map resultMap = Maps.newHashMap();
        Order order = orderMapper.selectByOrderNoAndUserId(orderNo, userId);
        if (order==null){
            return ServerResponse.createByError("该用户没有此订单");
        }

        //生成预订单信息
        //订单的交易号
        Long outTradeNo = order.getOrderNo();
        String subject = new StringBuilder().append("商城扫码支付:").append(order.getOrderNo()).toString();
        String totalAmount = order.getPayment().toString();
        String undiscountableAmount = "0";
        String timeOutExpress = "60m";
        //订单描述
        String body = new StringBuilder().append("订单号:").append(outTradeNo).append("购买商品总金额为")
                .append(totalAmount).toString();
        //以下保持支付宝Demo的默认信息
        String operatorId = "test_operator_id";
        String storeId = "test_store_id";
        List<GoodsDetail> goodsDetails = generateGoodsDetail(outTradeNo,userId);
        //发送数据，请求支付宝接口
        Configs.init("zfbinfo.properties");
        AlipayTradeService tradeService = new AlipayTradeServiceImpl.ClientBuilder().build();

        AlipayTradePrecreateRequestBuilder builder = new AlipayTradePrecreateRequestBuilder()
                .setSubject(subject).setTotalAmount(totalAmount)
                .setUndiscountableAmount(undiscountableAmount).setSellerId("")
                .setBody(body).setOperatorId(operatorId).setStoreId(storeId)
                .setTimeoutExpress(timeOutExpress).setGoodsDetailList(goodsDetails)
                .setNotifyUrl(PropertiesUtil.getProperty("alipay.callback.url",""));
        AlipayF2FPrecreateResult result = tradeService.tradePrecreate(builder);
        //拿到数据,根据数据生成二维码
        switch (result.getTradeStatus()) {
            case SUCCESS:
                logger.info("支付宝预下单成功: )");

                AlipayTradePrecreateResponse response = result.getResponse();

                File folder = new File(path);
                if (!folder.exists()){
                    folder.setWritable(true);
                    folder.mkdir();
                }
                String qrPath = String.format(path+"/qr-%s.png",response.getOutTradeNo());
                String qrFileName = String.format("/qr-%s.png",response.getOutTradeNo());
                ZxingUtils.getQRCodeImge(response.getQrCode(), 256, qrPath);

                File targetFile = new File(qrPath);
                boolean success = FtpUtil.uploadFile(Lists.newArrayList(targetFile));
                if (!success){
                    logger.error("上传二维码出现异常");
                    return ServerResponse.createByError("二维码模块出现异常");
                }
                String qrUrl = PropertiesUtil.getProperty("ftp.server.http.perfix","");
                resultMap.put("qrPath",qrUrl);
                return ServerResponse.createBySuccess(resultMap);

            case FAILED:
                logger.error("支付宝预下单失败!!!");
                return ServerResponse.createByError("支付宝预下单失败!!!");
            case UNKNOWN:
                logger.error("系统异常，预下单状态未知!!!");
                return ServerResponse.createByError("系统异常，预下单状态未知!!!");

            default:
                logger.error("不支持的交易状态，交易返回异常!!!");
                return ServerResponse.createByError("不支持的交易状态，交易返回异常!!!");
        }
        //将二维码生成到upload中，之后上传到图片服务器后删除.
        //将数据返回

    }

    private List<GoodsDetail> generateGoodsDetail(Long orderId,Integer userId){
        List<OrderItem> orderItemList = orderItemMapper.selectOrderItemsByOrderNoAndUserId(orderId,userId);
        List<GoodsDetail> goodsDetails = Lists.newArrayList();
        for (OrderItem item : orderItemList){
            GoodsDetail detail = GoodsDetail.newInstance(item.getProductId().toString(),
                    item.getProductName(), BigDecimalUtil.mul(item.getCurrentUnitPrice().doubleValue(),new Double(item.getQuantity()).doubleValue()).longValue(),
                    item.getQuantity());

            goodsDetails.add(detail);
        }
        return goodsDetails;
    }

    /**
     * 需要处理三个事情
     * 1. 商家交易号是否是正确的
     * 2. 处理支付宝的重复回调
     * 3. 如果是正确的回调需要修改订单状态，如果是修改成功需要
     * */
    @Override
    public ServerResponse checkOrderInfo(Map<String, String> params){
        Long orderNo = Long.parseLong(params.get("out_trade_no"));
        String tradeNo = params.get("trade_no");
        String tradeStatus = params.get("trade_status");
        Order order = orderMapper.selectByOrderNo(orderNo);
        if (Objects.isNull(order)){
            return ServerResponse.createByError("非商城的订单，回调忽略");
        }

        if (order.getStatus()>= Const.OrderStatusEnum.PAID.getCode()){
            return ServerResponse.createByError("支付宝重复调用");
        }

        if (Const.AlipayCallBackInfo.TRADE_STATUS_SUCCESS.equals(tradeNo)){
            order.setStatus(Const.OrderStatusEnum.PAID.getCode());
            order.setPaymentTime(DateTimeUtil.strToDate(params.get("gmt_payment")));
            orderMapper.updateByPrimaryKeySelective(order);
        }

        //组装订单信息到数据库
        PayInfo payInfo = new PayInfo();
        payInfo.setUserId(order.getUserId());
        payInfo.setOrderNo(order.getOrderNo());
        payInfo.setPayPlatform(Const.PayPlatformEnum.ALIPAY.getCode());
        payInfo.setPlatformNumber(tradeNo);
        payInfo.setPlatformStatus(tradeStatus);
        payInfoMapper.insert(payInfo);

        return ServerResponse.createBySuccess();
    }

    @Override
    public ServerResponse queryOrderStatus(Integer userId, Long orderNo) {
        Order order = orderMapper.selectByOrderNoAndUserId(orderNo,userId);
        if (Objects.isNull(order)){
            return ServerResponse.createByError("用户没有该订单");
        }
        if (order.getStatus()>=Const.OrderStatusEnum.PAID.getCode()){
            return ServerResponse.createBySuccess("用户支付成功");
        }
        return ServerResponse.createByError();
    }

    @Override
    public ServerResponse cancelOrder(Integer userId, Long orderNo){
        Order order = orderMapper.selectByOrderNoAndUserId(orderNo,userId);
        if (Objects.isNull(order)){
            return ServerResponse.createByError("该用户订单不存在");
        }
        if (order.getStatus()>=Const.OrderStatusEnum.PAID.getCode()){
            return ServerResponse.createByError("商品已经付款！");
        }
        Order updateOrder = new Order();
        updateOrder.setId(order.getId());
        updateOrder.setStatus(Const.OrderStatusEnum.CANCELED.getCode());
        int row = orderMapper.updateByPrimaryKeySelective(updateOrder);
        if (row>0){
            return ServerResponse.createBySuccess("取消订单成功");
        }
        return ServerResponse.createByError("取消订单过程发生错误");
    }

    @Override
    public ServerResponse getOrderCartProduct(Integer userId){
        OrderProductVo orderProductVo = new OrderProductVo();
        List<Cart> cartList = cartMapper.selectCheckedCartByUserId(userId);
        ServerResponse serverResponse = this.getCartOrderItem(userId,cartList);
        if (!serverResponse.isSuccess()){
            return serverResponse;
        }
        List<OrderItem> orderItemList = (List<OrderItem>) serverResponse.getData();
        List<OrderItemVo> orderItemVoList = Lists.newArrayList();
        BigDecimal payment = new BigDecimal("0");
        for (OrderItem orderItem:orderItemList){
            payment = BigDecimalUtil.add(payment.doubleValue(),orderItem.getTotalPrice().doubleValue());
            orderItemVoList.add(assambleOrderItemVo(orderItem));
        }
        orderProductVo.setProductTotalProce(payment);
        orderProductVo.setOrderItemVoList(orderItemVoList);
        orderProductVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix",""));
        return ServerResponse.createBySuccess(orderItemList);
    }

    @Override
    public ServerResponse<OrderVo> getOrderDetail(Integer userId, Long orderNo){
        Order order = orderMapper.selectByOrderNoAndUserId(orderNo,userId);
        if (Objects.isNull(order)){
            return ServerResponse.createByError("该用户下没有改订单");
        }
        List<OrderItem> orderItemList = orderItemMapper.selectOrderItemsByOrderNoAndUserId(orderNo,userId);
        OrderVo orderVo = assambleOrderVo(order,orderItemList);
        return ServerResponse.createBySuccess(orderVo);
    }

    @Override
    public ServerResponse list(Integer userId,Integer pageNum,Integer pageSize){
        PageHelper.startPage(pageNum,pageSize);
        List<Order> orderList = orderMapper.selectByUserId(userId);
        List<OrderVo> orderVoList = assambleOrderVoList(orderList,userId);
        PageInfo pageResult = new PageInfo(orderVoList);
        pageResult.setList(orderVoList);
        return ServerResponse.createBySuccess(orderVoList);
    }

    /**
     * 根据获得的Order的List进行处理
     * 如果不传入userId的话，就是管理员，可以查询所有的
     * */
    public List<OrderVo> assambleOrderVoList(List<Order> orderList,Integer userId){
        List<OrderVo> orderVoList = Lists.newArrayList();
        for (Order order:orderList){
            List<OrderItem> orderItems = null;
            if (userId==null){
                orderItems = orderItemMapper.selectByOrderNo(order.getOrderNo());
            }else {
                orderItems = orderItemMapper.selectOrderItemsByOrderNoAndUserId(order.getOrderNo(),
                        userId);
            }
            OrderVo orderVo = assambleOrderVo(order,orderItems);
            orderVoList.add(orderVo);
        }
        return orderVoList;
    }



    //BACKEND
    @Override
    public ServerResponse<PageInfo> manageList(int pageNum, int pageSize){
        PageHelper.startPage(pageNum,pageSize);
        List<Order> orderList = orderMapper.selectAllOrder();
        List<OrderVo> orderVoList = this.assambleOrderVoList(orderList,null);
        PageInfo pageResult = new PageInfo(orderList);
        pageResult.setList(orderVoList);
        return ServerResponse.createBySuccess(pageResult);
    }

    @Override
    public ServerResponse<OrderVo> manageDetail(Long orderNo){
        Order order = orderMapper.selectByOrderNo(orderNo);
        if (order!=null){
            List<OrderItem> orderItemList = orderItemMapper.selectByOrderNo(orderNo);
            OrderVo orderVo = assambleOrderVo(order,orderItemList);
            return ServerResponse.createBySuccess(orderVo);
        }
        return ServerResponse.createByError("订单不存在");
    }

    @Override
    public ServerResponse<PageInfo> manageSearch(Long orderNo, int pageNum, int pageSize){
        PageHelper.startPage(pageNum,pageSize);
        Order order = orderMapper.selectByOrderNo(orderNo);
        if (order!=null){
            List<OrderItem> orderItemList = orderItemMapper.selectByOrderNo(orderNo);
            OrderVo orderVo = assambleOrderVo(order,orderItemList);
            PageInfo pageResult = new PageInfo(Lists.newArrayList(orderVo));
            return ServerResponse.createBySuccess(pageResult);
        }
        return ServerResponse.createByError("订单不存在");
    }

    @Override
    public ServerResponse<String> manageSendGoods(Long orderNo){
        Order order = orderMapper.selectByOrderNo(orderNo);
        if (order!=null){
            if (order.getStatus()==Const.OrderStatusEnum.PAID.getCode()){
                Order orderTemp = new Order();
                orderTemp.setId(order.getId());
                orderTemp.setStatus(Const.OrderStatusEnum.SHIPPED.getCode());
                orderTemp.setSendTime(new Date());
                orderMapper.updateByPrimaryKeySelective(orderTemp);
                return ServerResponse.createBySuccess("发货成功");
            }
        }
        return ServerResponse.createByError("订单不存在");
    }
}
