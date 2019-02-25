package com.mmall.util;

import java.math.BigDecimal;

/**
 * @author Maoxin
 * @ClassName BigDecimalUtil
 * @date 2/25/2019
 */
public class BigDecimalUtil {

    private BigDecimalUtil(){}

    private BigDecimal add(double v1,double v2){
        BigDecimal b1 = new BigDecimal(Double.toString(v1));
        BigDecimal b2 = new BigDecimal(Double.toString(v2));
        return b1.add(b2);
    }
    private BigDecimal sub(double v1,double v2){
        BigDecimal b1 = new BigDecimal(Double.toString(v1));
        BigDecimal b2 = new BigDecimal(Double.toString(v2));
        return b1.subtract(b2);
    }
    private BigDecimal mul(double v1,double v2){
        BigDecimal b1 = new BigDecimal(Double.toString(v1));
        BigDecimal b2 = new BigDecimal(Double.toString(v2));
        return b1.multiply(b2);
    }
    private BigDecimal div(double v1,double v2){
        BigDecimal b1 = new BigDecimal(Double.toString(v1));
        BigDecimal b2 = new BigDecimal(Double.toString(v2));
        //需要考虑到除不尽的情况,指明两位小数并且四舍五入
        return b1.divide(b2,2,BigDecimal.ROUND_HALF_UP);
    }


}
