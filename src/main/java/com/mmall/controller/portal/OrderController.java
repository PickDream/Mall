package com.mmall.controller.portal;

import com.alipay.api.AlipayApiException;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.demo.trade.config.Configs;
import com.google.common.collect.Maps;
import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;
import com.mmall.service.IOrderService;
import com.mmall.service.IShipingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;

/**
 * @author Maoxin
 * @date 2/28/2019
 */

@Controller
@RequestMapping("/order/")
public class OrderController {

    private static final Logger logger = LoggerFactory.getLogger(OrderController.class);
    @Autowired
    IOrderService iOrderService;
    @Autowired
    IShipingService iShipingService;

    @ResponseBody
    @RequestMapping
    public ServerResponse createOrder(HttpSession session,Integer shippingId){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录，请登录");
        }
        return iOrderService.createOrder(user.getId(),shippingId);
    }
    /**
     * 支付接口
     * @param orderId 订单号;
     * */
    @ResponseBody
    @RequestMapping("pay.do")
    public ServerResponse pay(HttpSession session, Long orderId, HttpServletRequest request){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录，请登录");
        }
        String path = request.getSession().getServletContext().getRealPath("upload");
        return iOrderService.pay(orderId,user.getId(),path);
    }

    @ResponseBody
    @RequestMapping("alipay_callback.do")
    public Object alipayCallBack(HttpServletRequest request){
        Map<String,String> params = Maps.newHashMap();
        Map requestParms = request.getParameterMap();
        for (Iterator iterator = requestParms.keySet().iterator();iterator.hasNext();){
            String name = (String) iterator.next();
            String[] values = (String[]) requestParms.get(name);
            String valueStr = "";
            for (int i=0;i<values.length;i++){
                valueStr = (i==values.length-1)?valueStr+values[i]:valueStr+values[i]+',';
            }
            params.put(name,valueStr);
        }
        //logger.info()
        //根据支付宝的要求删除掉sign_type
        params.remove("sign_type");
        //验证回调的正确性，是不是支付宝发的，
        try {
            boolean alipayRSACheckedV2 = AlipaySignature.rsaCheckV2(params, Configs.getAlipayPublicKey(),"utf-8",Configs.getSignType());
            if (!alipayRSACheckedV2){
                return ServerResponse.createByError("非法请求，验证不通过");
            }
        } catch (AlipayApiException e) {
            e.printStackTrace();
            logger.error("支付宝验证回调异常");
        }
        //关于支付宝验签,相关信息，https://alipay.open.taobao.com/docs/doc.htm?spm=a219a.7629140.0.0.mFogPC&treeId=193&articleId=103296&docType=1
        //支付宝验签之后，任然需要做检验
        ServerResponse serverResponse = iOrderService.checkOrderInfo(params);
        if (serverResponse.isSuccess()){
            return Const.AlipayCallBackInfo.RESPONSE_SUCCESS;
        }
        return Const.AlipayCallBackInfo.RESPONSE_FAILED;
    }

    @ResponseBody
    @RequestMapping("query_order_pay_status.do")
    public ServerResponse<Boolean> queryOrderPayStatus(HttpSession session, Long orderNo){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (Objects.isNull(user)){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return iOrderService.queryOrderStatus(user.getId(),orderNo);
    }

    @ResponseBody
    @RequestMapping("cancel.do")
    public ServerResponse create(HttpSession session, Long orderNo) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (Objects.isNull(user)) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
        }
        return iOrderService.cancelOrder(user.getId(), orderNo);
    }

    @ResponseBody
    @RequestMapping("get_order_cart_product.do")
    public ServerResponse getOrderCartProduct(HttpSession session) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (Objects.isNull(user)) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
        }
        return iOrderService.getOrderCartProduct(user.getId());
    }

    @ResponseBody
    @RequestMapping("detail.do")
    public ServerResponse detail(HttpSession session,Long orderNo){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (Objects.isNull(user)) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
        }
        return iOrderService.getOrderDetail(user.getId(),orderNo);
    }

    @ResponseBody
    @RequestMapping("list.do")
    public ServerResponse list(HttpSession session, @RequestParam(value = "pageSize",defaultValue = "10")Integer pageSize,
                               @RequestParam(value = "pageNum",defaultValue = "1")Integer pageNum){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (Objects.isNull(user)) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
        }
        return iOrderService.list(user.getId(),pageNum,pageSize);
    }
}
