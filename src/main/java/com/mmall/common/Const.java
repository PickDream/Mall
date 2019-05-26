package com.mmall.common;

import com.google.common.collect.Sets;
import com.mmall.pojo.Product;

import java.util.Set;

/**
 * 常量类，用于维护分布在其他位置的常量
 * @author Maoxin
 * @date 1/28/2019
 */
public class Const {
    public static final String CURRENT_USER = "currentUser";
    public static final String USER_NAME ="username";
    public static final String EMAIL = "email";
    public static final String TOKEN_PREFIX="token_";
    public interface Role{
        int ROLE_CUSTOMER = 0;
        int ROLE_ADMIN = 1;
    }
    public interface ProductListOrderBy{
        Set<String> PRICE_ASC_DESC = Sets.newHashSet("price_desc","price_asc");
    }
    //代指商品是否已经被选中
    public interface Cart{
        int CHECKED = 1;
        int UNCHECKED = 0;
        String LIMIT_NUM_FAIL = "LIMIT_NUM_FAIL";
        String LIMIT_NUM_SUCCESS="LIMIT_NUM_SUCCESS";
    }
    public interface RedisCacheConfig{
        int REDIS_SESSION_EXTIME = 60*30;
    }

    public enum ProductStatusEnum{
        ON_SALE(1,"在线");
        private String value;
        private int code;
        ProductStatusEnum(int code,String value){
            this.code = code;
            this.value = value;
        }

        public String getValue(){return value;}

        public int getCode(){return  code;}
    }

    public enum OrderStatusEnum{

        CANCELED(0,"订单已经取消"),
        NOT_PAY(10,"未付款"),
        PAID(20,"已经付款"),
        SHIPPED(40,"发货成功"),
        SUCCESSED(50,"交易成功"),
        CLOSED(60,"交易关闭");

        OrderStatusEnum(int code,String value){
            this.code = code;
            this.value = value;
        }
        private String value;
        private int code;

        public String getValue() {
            return value;
        }

        public int getCode() {
            return code;
        }

        public static OrderStatusEnum codeOf(int code){
            for (OrderStatusEnum orderStatusEnum:OrderStatusEnum.values()){
                if (orderStatusEnum.getCode()==code){
                    return orderStatusEnum;
                }
            }
            throw new RuntimeException("没有找到对应的枚举");
        }
    }

    /**
     * 支付宝交易状态码参见
     * https://alipay.open.taobao.com/docs/doc.htm?spm=a219a.7629140.0.0.mFogPC&treeId=193&articleId=103296&docType=1#s4
     * */
    public interface AlipayCallBackInfo{
        String TRADE_STATUS_WAIT_BUYER_PAY = "WAIT_BUYER_PAY";
        String TRADE_STATUS_CLOSED= "TRADE_CLOSED";
        String TRADE_STATUS_SUCCESS= "TRADE_SUCCESS";
        String TRADE_STATUS_FINISHED= "TRADE_FINISHED";

        String RESPONSE_SUCCESS = "success";
        String RESPONSE_FAILED = "failed";
    }

    public enum PayPlatformEnum{
        ALIPAY(1,"支付宝");
        PayPlatformEnum(int code,String value){
            this.code = code;
            this.value = value;
        }
        private int code;
        private String value;

        public int getCode() {
            return code;
        }

        public String getValue() {
            return value;
        }
    }

    public enum PaymentTypeEnum{
        ONLINE_PAY(1,"在线支付");
        PaymentTypeEnum(int code,String value){
            this.code = code;
            this.value = value;
        }
        private String value;
        private int code;

        public String getValue() {
            return value;
        }

        public int getCode() {
            return code;
        }

        public static PaymentTypeEnum codeOf(int code){
            for (PaymentTypeEnum paymentTypeEnum:PaymentTypeEnum.values()){
                if (paymentTypeEnum.getCode()==code){
                    return paymentTypeEnum;
                }
            }
            throw  new RuntimeException("没有找到对应的枚举类");
        }
    }

}
