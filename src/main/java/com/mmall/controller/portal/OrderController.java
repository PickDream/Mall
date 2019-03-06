package com.mmall.controller.portal;

import com.google.common.collect.Maps;
import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;
import com.mmall.service.IOrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Iterator;
import java.util.Map;

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
    @RequestMapping("")
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
        //验证回调的正确性，是不是支付宝发的，并且还需要避免重复通知
        //关于支付宝验签,相关信息，https://alipay.open.taobao.com/docs/doc.htm?spm=a219a.7629140.0.0.mFogPC&treeId=193&articleId=103296&docType=1

        return null;
    }
}
