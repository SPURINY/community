package com.nowcoder.community.controller;

import com.google.code.kaptcha.Producer;
import com.nowcoder.community.dao.LoginTicketMapper;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.UserService;
import com.nowcoder.community.util.CommunityConstant;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Map;

@Controller
public class LoginController implements CommunityConstant {

    private static final Logger logger= LoggerFactory.getLogger(LoginController.class);//打日志

    @Autowired
    private UserService userService;
    @Autowired
    private Producer kaptchaProducer;//注入

    @Value("${server.servlet.context-path}")
    private String contextPath;

    @RequestMapping(path="register",method = RequestMethod.GET)
    public String getRegisterPage(){
        return"/site/register";
    }

    @RequestMapping(path="login",method=RequestMethod.GET)
    public String getLoginPage(){ return "/site/login";}

    @RequestMapping(path="register",method=RequestMethod.POST)
    public String register(Model model, User user){//model携带数据传给模板
        //springmvc会自动把值注入给形参里的user对象
        Map<String, Object> map = userService.register(user);//根据返回的map进一步选择给浏览器做出怎样的响应
        //System.out.println("Controller-是否map==null:"+(map==null));//false,所以问题出在返回的map为空
        //System.out.println(map.toString());//{}
        if(map==null||map.isEmpty()){//注册成功[注册成功后跳转到首页，激活成功后跳到登录]
            model.addAttribute("msg","注册成功，请在邮箱激活账号");
            model.addAttribute("target","/index");
            //System.out.println("map==null"+(map==null));这句没输出，合理怀疑map!=null
            return "/site/operate-result";//动态的中间人模板页面
        }else{//注册失败
            model.addAttribute("usernameMsg",map.get("usernameMsg"));
            model.addAttribute("emailMsg",map.get("emailMsg"));
            model.addAttribute("passwordMsg",map.get("passwordMsg"));
            return "/site/register";
            /*当注册失败跳回register页面时，页面可以直接使用user对象(形参)，
            因为springmv在走这个方法时就把user放到了model里*/
        }

    }
    //激活路径：String url=domain+contextPath+"/activation/"+user.getId()+"/"+user.getActivationCode();
    @RequestMapping(path="activation/{userId}/{code}",method = RequestMethod.GET)
    public String activation(Model model, @PathVariable("userId")int userId,
                             @PathVariable("code")String code){//从路径中取值当参数，@PathVariable
        int res = userService.activation(userId, code);
        System.out.println("激活结果："+res);//激活结果：2,激活失败
        if(res==ACTIVATION_SUCCESSS){
            model.addAttribute("msg","激活成功，跳转到登陆页面");
            model.addAttribute("target","/login");
        }else if(res==ACTIVATION_REPEAT){
            model.addAttribute("msg","这是无效的操作，账号已经被激活过了");
            model.addAttribute("target","/index");
        }else{
            model.addAttribute("msg","激活失败，激活码不正确");
            model.addAttribute("target","/index");

        }
        return "/site/operate-result";//中间过渡页面

    }

    @RequestMapping(path="kaptcha",method=RequestMethod.GET)
    public void kaptcha(HttpServletResponse response, HttpSession session){
        //不需要返回页面或字符串，利用rsponse，所以返回值void
        //生成的验证码放在客户端不安全，放到服务器，所以有多个请求间的联系，用cookie和session技术
        //生成验证码
        String text = kaptchaProducer.createText();
        BufferedImage image = kaptchaProducer.createImage(text);
        //把文字验证码存到session
        session.setAttribute("kaptcha",text);
        //图片给浏览器显示出来
        response.setContentType("image/png");
        try {
            ServletOutputStream stream = response.getOutputStream();//图片用字节流比较好
            ImageIO.write(image,"png",stream);
        } catch (IOException e) {
            logger.error("相应验证码失败"+e.getMessage());
        }
    }

    @RequestMapping(path="login",method=RequestMethod.POST)
    public String login(String username,String password,String code,boolean remember,Model model,HttpSession session,HttpServletResponse response){
        //从表单拿到形参这些数据，code验证码，remem是否勾选”记住我“-->expired时间会长一些
        //之前kaptcha生成的验证码放到了session里，所以参数要用HttpSession来取验证码，来和用户输入的验证码比对
        //如果登陆成功，服务器要把ticket交给客户端保存，利用cookie，所以参数要写HttpServletResponse

        //检查验证码
        String kaptcha =(String) session.getAttribute("kaptcha");
        if(StringUtils.isBlank(kaptcha)||StringUtils.isBlank(code)||!kaptcha.equalsIgnoreCase(code)){
            model.addAttribute("codeMsg","验证码错误");
            return "/site/login";
        }
        //检查账号密码（调用业务层）
        int expiredSeconds=remember?REMEMBER_EXPIRED_SECONDS:DEFAULT_EXPIRED_SECONDS;
        Map<String, Object> map = userService.login(username, password, expiredSeconds);
        if(map.containsKey("ticket")){//登陆成功
            Cookie cookie = new Cookie("ticket", map.get("ticket").toString());//用cookie存ticket
            cookie.setMaxAge(expiredSeconds);
            cookie.setPath(contextPath);
            response.addCookie(cookie);

            return "index";
        }else{
            model.addAttribute("usernameMsg",map.get("usernameMsg"));
            model.addAttribute("passwordMsg",map.get("passwordMsg"));
            return "/site/login";
        }


    }


}
