package com.nowcoder.community.util;

import org.apache.commons.lang.StringUtils;
import org.springframework.util.DigestUtils;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class CommunityUtil {

    public static String getUUID(){
        return UUID.randomUUID().toString().replaceAll("-","");
    }

    /*MD5加密  加密后的字符串+随机字符串，更安全点*/
    public static String md5(String key){
        if(StringUtils.isBlank(key)) return null;

        return DigestUtils.md5DigestAsHex(key.getBytes());
    }
}
