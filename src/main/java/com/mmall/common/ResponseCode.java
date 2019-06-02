package com.mmall.common;

import org.apache.ibatis.javassist.runtime.Desc;

/**
 * 响应编码枚举类
 * @author Maoxin
 * @date 1/28/2019
 */
public enum  ResponseCode {
    SUCCESS(0,"SUCCESS"),
    ERROR(1,"ERROR"),
    NEED_LOGIN(10,"NEED_LOGIN"),
    IllEGAL_ARGUMENT(2,"ILLEGAL_ARGUMENT");

    private final int code;
    private final String desc;
    ResponseCode(int code,String desc){
        this.code = code;
        this.desc = desc;
    }

    public int getCode(){
        return code;
    }
    public String getDesc(){
        return desc;
    }
}
