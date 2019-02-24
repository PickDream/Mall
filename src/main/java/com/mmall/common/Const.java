package com.mmall.common;

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
}
