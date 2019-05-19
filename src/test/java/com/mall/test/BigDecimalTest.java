package com.mall.test;

import org.junit.Test;

import java.math.BigDecimal;

/**
 * @author Maoxin
 * @ClassName BigDecimalTest
 * @date 2/25/2019
 */
public class BigDecimalTest {

    @Test
    public void test4(){
        System.out.println(Double.toString(21.2123132));
        System.out.println(Double.toString(23.1212121));
        BigDecimal bigDecimalA = new BigDecimal(Double.toString(21.2123132));
        BigDecimal bigDecimalB = new BigDecimal(Double.toString(23.1212121));

        System.out.println(bigDecimalA.add(bigDecimalB));
    }

}
