package com.nowcoder.community.util;
/*
* 常量接口
* */
public interface CommunityConstant {
    /*
    * 激活常量
    * */
    int ACTIVATION_SUCCESSS=0;//成功
    int ACTIVATION_REPEAT=1;//重复
    int ACTIVATION_FAILURE=2;//失败
    /*
    * expired 时间
    * */
    //默认
    int DEFAULT_EXPIRED_SECONDS=3600*12;
    //勾选了 记住我
    int REMEMBER_EXPIRED_SECONDS=3600*30*24;
}
