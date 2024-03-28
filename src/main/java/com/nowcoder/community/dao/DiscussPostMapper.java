package com.nowcoder.community.dao;

import com.nowcoder.community.entity.DiscussPost;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
public interface DiscussPostMapper {

    //查询多个帖子
    //这个方法①不传id：即看首页帖子时，id没传所以是0，不用拼接sql
    //②看自己主页的帖子：即传了userid=xxx，需要拼接sql----->动态sql
    //考录到未来加分页显示的可能性：int offset该页起始,int limit每页显示多少条
    List<DiscussPost>selectPosts(int userId,int offset,int limit);

    //查看帖子总数（可用于计算页数）
    //传id：自己多少条帖子；不传
    //@Param("userid"):如果需要动态sql<if>+只有一个形参--》必须用@Param("userid")起别名
    int selectPostRows(@Param("userId") int userId);
}
