package com.mmall.common;

import com.mmall.pojo.Product;

/**
 * 常量类，用于维护分布在其他位置的常量
 * @author Maoxin
 * @date 1/28/2019
 */
public class Const {
    public static final String CURRENT_USER = "currentUser";
    public static final String USER_NAME ="userName";
    public static final String EMAIL = "email";
    public interface Role{
        int ROLE_CUSTOMER = 0;
        int ROLE_ADMIN = 1;
    }
    //代指商品是否已经被选中
    public interface Cart{
        int CHECKED = 1;
        int UNCHECKED = 0;
        String LIMIT_NUM_FAIL = "LIMIT_NUM_FAIL";
        String LIMIT_NUM_SUCCESS="LIMIT_NUM_SUCCESS";
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

}
