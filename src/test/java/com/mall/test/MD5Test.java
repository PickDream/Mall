package com.mall.test;

import com.mmall.util.MD5Util;
import org.junit.Test;

/**
 * @author Maoxin
 * @ClassName MD5Test
 * @date 4/14/2019
 */
public class MD5Test {
    @Test
    public void test(){
        String password = MD5Util.MD5EncodeUtf8("maoxin");
        System.out.println(password);
    }
}
