package com.nowcoder.community.controller;

import com.nowcoder.community.annotation.LoginRequired;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.UserService;
import com.nowcoder.community.util.CommunityUtil;
import com.nowcoder.community.util.HostHolder;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

@Controller
@RequestMapping("/user")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Value("${server.servlet.context-path}")
    private String contextPath;
    @Value("${community.path.upload}")
    private String uploadPath;
    @Value("${community.path.domain}")
    private String domain;

    @Autowired
    private UserService userService;
    @Autowired
    private HostHolder hostHolder;//修改头像是修改当前用户的头像，还得获取当前用户

    @LoginRequired
    @RequestMapping(path="/setting",method = RequestMethod.GET)
    public String getSettingPage(){
        return "/site/setting";
    }

    @LoginRequired
    @RequestMapping(path = "/upload",method = RequestMethod.POST)
    public String uploadHeader(MultipartFile headerImage, Model model){//model给模板传值
        if(headerImage==null){
            model.addAttribute("error","您还未选择图片");
            return "/site/setting";
        }
        String fileName = headerImage.getOriginalFilename();
        String suffix = fileName.substring(fileName.lastIndexOf("."));//后缀“.png”
        if(StringUtils.isBlank(suffix)){//图片后缀为空
            model.addAttribute("error","文件格式不正确");
            return "/site/setting";
        }
        fileName=CommunityUtil.generateUUID()+suffix;//新的filename（随机生成+原来后缀）
        File file = new File(uploadPath + "/" + fileName);//上传文件后，该文件的路径(存在本地路径
        try {
            //存储文件
            headerImage.transferTo(file);
        } catch (IOException e) {
            logger.debug("上传文件失败"+e.getMessage());
            throw new RuntimeException(e);
        }
        //更新用户头像的url
        //http://localhost:8080/community/user/header/xxx.png静态资源
        User user=hostHolder.getUser();
        String headerUrl=domain+contextPath+"/user/header/"+fileName;
        userService.updateHeader(user.getId(),headerUrl);

        return "redirect:/index";
    }

    //获取头像
    // http://localhost:8080/community/user/header/xxx.png
    @RequestMapping(path="/header/{fileName}",method = RequestMethod.GET)
    public void getHeader(@PathVariable("fileName")String fileName, HttpServletResponse response) throws IOException {
        //因为借助response向浏览器输出图片，既不是页面也不是字符串，所以用void

        fileName=uploadPath+"/"+fileName;//在服务器的路径（即本地
        String suffix=fileName.substring(fileName.lastIndexOf("."));
        response.setContentType("image/"+suffix);//响应图片

        FileInputStream is = null;
        try {
            ServletOutputStream os = response.getOutputStream();
            is = new FileInputStream(fileName);
            int b=0;
            byte[] buffer = new byte[1024];//定义缓冲区
            while ((b=is.read(buffer))!=-1) {//读到末尾会返回-1;没到末尾则 返回字节表示的int值
                os.write(buffer,0,b);
            }
        } catch (IOException e) {
            logger.error("读取文件失败:"+e.getMessage());
            throw new RuntimeException(e);
        }finally{
            is.close();//因为FileInputStream是自己创建的，所以要 手动关闭
        }

    }

    @LoginRequired
    @RequestMapping(path="updatePsw",method=RequestMethod.POST)
    public String updatePassword(String oldpsw,String newpsw,String repsw,Model model){
        User user=hostHolder.getUser();
        String psw=CommunityUtil.md5(oldpsw+user.getSalt());//密码是加密存的
        if(!user.getPassword().equals(psw)){
            model.addAttribute("pswMsg","原密码不正确");
            return "site/setting";
        }
        if(!newpsw.equals(repsw)){
            model.addAttribute("pswMsg","两次密码不一致");
            return "site/setting";
        }
        userService.updatePassword(user.getId(),newpsw);
        return "redirect:/index";
    }
}
