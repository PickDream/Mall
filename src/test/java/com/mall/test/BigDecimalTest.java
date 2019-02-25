package com.mall.test;

import org.junit.Test;

import java.math.BigDecimal;

/**
 * @author Maoxin
 * @ClassName BigDecimalTest
 * @date 2/25/2019
 */
public class BigDecimalTest {
//    @Test
//    public void test(){
//        System.out.println(0.05+0.01);
//        System.out.println(1.0-0.42);
//        System.out.println(4.015*100);
//        System.out.println(123.3/100);
//    }
//    @Test
//    public void test2(){
//        BigDecimal b1 = new BigDecimal(0.05);
//        BigDecimal b2 = new BigDecimal(0.01);
//        System.out.println(b1.add(b2));
//    }
//    @Test
//    public void test3(){
//        BigDecimal b1 = new BigDecimal("0.05");
//        BigDecimal b2 = new BigDecimal("0.01");
//        System.out.println(b1.add(b2));
//    }
    @Test
    public void test4(){
        System.out.println(Double.toString(21.2123132));
        System.out.println(Double.toString(23.1212121));
        BigDecimal bigDecimalA = new BigDecimal(Double.toString(21.2123132));
        BigDecimal bigDecimalB = new BigDecimal(Double.toString(23.1212121));

        System.out.println(bigDecimalA.add(bigDecimalB));
    }
}
