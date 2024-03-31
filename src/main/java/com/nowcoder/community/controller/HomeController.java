package com.nowcoder.community.controller;

import com.nowcoder.community.entity.DiscussPost;
import com.nowcoder.community.entity.Page;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.DiscussPostService;
import com.nowcoder.community.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class HomeController {
    @Autowired
    private UserService userService;
    @Autowired
    private DiscussPostService discussPostService;

    @RequestMapping(path = "/index", method = RequestMethod.GET)
    public String getIndexPage(Model model, Page page){//使用模板
        page.setPath("/index");
        page.setTotalRows(discussPostService.selectDiscussPostRows(0));

        //这个查出来的post数据只有userid，没有username，所以后面用userService查出user，一起放map里了
        List<DiscussPost> list = discussPostService.selectDiscussPosts(0, page.getOffset(), page.getLimit());
        List<Map<String,Object>> posts = new ArrayList<>();

        if (list != null) {
            for (DiscussPost i : list) {
                Map<String, Object> map = new HashMap<>();
                //System.out.println(i.toString());
                map.put("post", i);//把帖子数据放入map
                //System.out.println(i.getId());
                User user = userService.getUser(i.getUserId());//获取id对应的用户实体对象
                System.out.println(user.toString());
                map.put("user", user);//把用户数据放入map

                posts.add(map);//把map放入posts列表
            }
        }
        model.addAttribute("posts",posts);
        System.out.println(page.getTotalRows()+"--totalRows");
        //model.addAttribute("page",page);
        return "index";//模板文件路径
    }


}
