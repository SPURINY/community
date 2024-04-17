package com.nowcoder.community.util;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

public class CookieUtil {
    /*查找cookiek对应的v*/
    public  static String getValue(HttpServletRequest request,String name){//request及要找的cookie的名字
        if(request==null||name==null){
            throw new IllegalArgumentException();
        }
        Cookie[] cookies = request.getCookies();
        if(cookies!=null){
            for(Cookie cookie:cookies){
                if(cookie.getName().equals(name)){
                    return cookie.getValue();
                }
            }
        }
        return null;
    }
}
