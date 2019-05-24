package com.mmall.util;


import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * CookieUtil
 * */
@Slf4j
public class CookieUtil {
    private final static String COOKIE_DOMAIN = ".maomall.com";
    private final static String COOKIE_NAME="mall_login_token";

    /**
     * 写入Cookie
     * */
    public static void writeLoginToken(HttpServletResponse response,String token){
        Cookie ck = new Cookie(COOKIE_NAME,token);
        ck.setDomain(COOKIE_DOMAIN);
        //设置路径为根目录
        ck.setPath("/");
        //单位是秒，如果这个maxage你设置的话,cookie就不会写进硬盘
        ck.setMaxAge(-1);
        //不允许前端脚本获取Cookie
        ck.setHttpOnly(true);
        log.info("write cookieName:{}.cookieValue:{}",ck.getName(),ck.getValue());
        response.addCookie(ck);
    }
    /**
     *
     * */
    public static String readLoginToken(HttpServletRequest request){
        Cookie[] cks = request.getCookies();
        if (cks!=null){
            for (Cookie cookie:cks){
                //log.info("read cookieName:{},cookieValue:{}",cookie.getName(),cookie.getValue());
                if (StringUtils.equals(COOKIE_NAME,cookie.getName())){
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    /**
     * 删除登录的Token
     * */
    public static void delLoginToken(HttpServletRequest request,HttpServletResponse response){
        Cookie[] cks = request.getCookies();
        if (cks!=null){
            for (Cookie ck:cks){
                if (StringUtils.equals(COOKIE_NAME,ck.getName())){
                    ck.setDomain(COOKIE_DOMAIN);
                    ck.setPath("/");
                    ck.setMaxAge(0);
                    response.addCookie(ck);
                    return;
                }
            }
        }
    }
}
