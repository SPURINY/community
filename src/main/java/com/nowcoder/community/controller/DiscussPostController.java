package com.nowcoder.community.controller;

import com.nowcoder.community.entity.DiscussPost;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.DiscussPostService;
import com.nowcoder.community.util.CommunityUtil;
import com.nowcoder.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Date;

@Controller
@RequestMapping("/discuss")
public class DiscussPostController {
    @Autowired
    private DiscussPostService discussPostService;
    @Autowired
    private HostHolder hostHolder;//持有用户数据
    @RequestMapping(path="/add",method= RequestMethod.POST)
    @ResponseBody
    public String addDiscussPost(String title,String content){//页面只需要传入标题、内容，userid从hostholder拿，其他的也可自动or默认
        User user = hostHolder.getUser();
        if(user==null){//还没登陆 403（没有权限）
            return CommunityUtil.getJSONString(403,"还未登录，没有权限发布帖子");
        }
        DiscussPost discussPost = new DiscussPost();
        discussPost.setUserId(user.getId());
        discussPost.setTitle(title);
        discussPost.setContent(content);
        discussPost.setCreateTime(new Date());
        discussPostService.insertDiscussPost(discussPost);
        //报错的情况将来统一处理
        return CommunityUtil.getJSONString(0,"发布成功");
    }

}
