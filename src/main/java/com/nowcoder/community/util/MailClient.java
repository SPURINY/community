package com.nowcoder.community.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

/*
封装发邮件
* 发送人：配置文件里的邮箱，为了方便还用@Value注入给了属性
* 收信人：
* */

@Component
public class MailClient {

    private static final Logger logger=LoggerFactory.getLogger(MailClient.class);

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String from;

    public void sendMail(String to,String subject,String content){
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper=new MimeMessageHelper(message);
            helper.setFrom(from);//发信人
            helper.setTo(to);//收信人
            helper.setSubject(subject);//邮件主题
            helper.setText(content,true);//邮件内容

            mailSender.send(helper.getMimeMessage());//发送邮件
        } catch (MessagingException e) {
            logger.error("发送邮件失败 "+e.getMessage());
        }
    }
}
