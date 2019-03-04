package com.mmall.service.impl;

import com.alipay.api.response.AlipayTradePrecreateResponse;
import com.alipay.demo.trade.config.Configs;
import com.alipay.demo.trade.model.GoodsDetail;
import com.alipay.demo.trade.model.builder.AlipayTradePrecreateRequestBuilder;
import com.alipay.demo.trade.model.result.AlipayF2FPrecreateResult;
import com.alipay.demo.trade.service.AlipayTradeService;
import com.alipay.demo.trade.service.impl.AlipayTradeServiceImpl;
import com.alipay.demo.trade.utils.ZxingUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mmall.common.ServerResponse;
import com.mmall.dao.OrderItemMapper;
import com.mmall.dao.OrderMapper;
import com.mmall.pojo.Order;
import com.mmall.pojo.OrderItem;
import com.mmall.service.IOrderService;
import com.mmall.util.BigDecimalUtil;
import com.mmall.util.FtpUtil;
import com.mmall.util.PropertiesUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.List;
import java.util.Map;

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

}
