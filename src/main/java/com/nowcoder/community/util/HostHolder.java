package com.nowcoder.community.util;

import com.nowcoder.community.entity.User;
import org.springframework.stereotype.Component;

/*
* 容器作用
* 持有用户信息，用于代替session对象
* */
@Component
public class HostHolder {
    //ThreadLocal set()get()源码以线程为key存取值（?）
    private ThreadLocal<User> users=new ThreadLocal<>();

    public  void setUser(User user){
        users.set(user);
    }

    public User getUser(){
        return users.get();
    }
    public void clear(){
        users.remove();//用完之后把当前线程的都清掉（防止占用太多资源
    }
}
