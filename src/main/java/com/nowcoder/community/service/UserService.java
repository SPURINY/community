package com.nowcoder.community.service;

import com.nowcoder.community.dao.UserMapper;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.util.CommunityConstant;
import com.nowcoder.community.util.CommunityUtil;
import com.nowcoder.community.util.MailClient;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
public class UserService implements CommunityConstant {
    //实现常量接口，因为要用激活常量
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private MailClient mailClient;
    @Autowired
    private TemplateEngine templateEngine;

    @Value("${community.path.domain}")
    private String domain;//域名
    @Value("${server.servlet.context-path}")
    private String contextPath;//项目名

    //
    public User getUser(int id){
        return userMapper.selectById(id);
    }

    //注册结果的反馈
    public Map<String,Object>register(User user) {//封装填写的注册信息为user对象
        Map<String, Object> map = new HashMap<>();
        //判断账号密码是否合法
        if(user==null){
            throw new IllegalArgumentException("参数不能为空");
        }
        //业务漏洞，所以没抛异常，把提示信息放起来map
        if(StringUtils.isBlank(user.getUsername())){
            map.put("usernameMsg","用户名不能为空");
            return map;
        }
        if(StringUtils.isBlank(user.getPassword())){
            map.put("passwordMsg","密码不能为空");
            return map;
        }
        if(StringUtils.isBlank(user.getEmail())){
            map.put("emailMsg","邮箱不能为空");
            return map;
        }

        //验证账号是否已经存在
        //因为主键id是自增的，所以不用设置，也就不用验证
        User u=userMapper.selectByName(user.getUsername());
        if(u!=null){
            map.put("usernameMsg","该账户已存在");
            return map;
        }
        u=userMapper.selectByEmail(user.getEmail());
        if(u!=null){
            map.put("emailMsg","该邮箱已存在");
            return map;
        }

        //全都没有问题，注册
        user.setSalt(CommunityUtil.getUUID().substring(0,5));//在密码后边拼的那串随机字符
        user.setPassword(CommunityUtil.md5(user.getPassword()+user.getSalt()));//存储加密后的密码
        user.setType(0);
        user.setStatus(0);//未被激活
        user.setActivationCode(CommunityUtil.getUUID());//激活码
        //默认头像,%d占位符 表示数字，new Random().nextInt(1000)生成1000内的随机数
        user.setHeaderUrl(String.format("http://images.nowcoder.com/head/%dt.png",new Random().nextInt(1000)));
        user.setCreateTime(new Date());
        //调用insert添加到数据库里
        userMapper.insertUser(user);

        //发激活邮件
        Context context = new Context();
        //因为对应的activation.html模板就是需要传入email和激活url，所以setVariable设置这俩
        context.setVariable("email",user.getEmail());
        //激活url需要动态拼出来
        String url=domain+contextPath+"/activation/"+user.getId()+"/"+user.getActivationCode();
        context.setVariable("url",url);
        String content=templateEngine.process("/mail/activation",context);//content邮件内容
        mailClient.sendMail(user.getEmail(),"激活账号",content);
        //System.out.println("service-是否map==null:"+(map==null));//false
        return map;//map为空，则没有任何问题
    }

    public int activation(int userId,String code){
        //先查有没有这个用户，再查激活码是否正确
        User user = userMapper.selectById(userId);
        System.out.println(user.getUsername());//purin
        System.out.println("status"+user.getStatus());//status0,说明目前没问题,问题出在没用equals，用了==
        if(user.getStatus()==1){
            return ACTIVATION_REPEAT;
        }else if(user.getActivationCode().equals(code)){
            userMapper.updateStatus(userId,1);//修改状态为激活成功
            //因为要修改数据库信息，所以用mapper，我差点就写成user.setStatus了，有个毛用
            return ACTIVATION_SUCCESSS;
        }else return ACTIVATION_FAILURE;
    }


}
