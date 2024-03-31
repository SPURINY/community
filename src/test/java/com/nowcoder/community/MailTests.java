package com.nowcoder.community;

import com.nowcoder.community.util.MailClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.HashMap;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class MailTests {

    @Autowired
    private MailClient mailClient;
    
    @Autowired
    private TemplateEngine templateEngine;

    @Test
    public void testTextMail(){
        mailClient.sendMail("3029567911@qq.com","test","test");
    }
    
    @Test
    public void testHtmlMail(){
        Context context = new Context();
        context.setVariable("username","purin^^");
        String content = templateEngine.process("/mail/demo.html", context);//用thymemleaf模板引擎生成html
        System.out.println(content);
        mailClient.sendMail("3029567911@qq.com","test2",content);//发邮件
    }

    @Test
    public void test01(){
        Map<String, Object> map = new HashMap<>();
        System.out.println(map==null);//map==null表示是否为map分配了地址
        System.out.println(map.isEmpty());//判断map是否为空用isEmpty()
    }}
