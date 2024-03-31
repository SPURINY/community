package com.nowcoder.community.controller;

import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.UserService;
import com.nowcoder.community.util.CommunityConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Map;

@Controller
public class LoginController implements CommunityConstant {
    @Autowired
    private UserService userService;

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



}
