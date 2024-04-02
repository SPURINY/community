package com.nowcoder.community.dao;

import com.nowcoder.community.entity.LoginTicket;
import org.apache.ibatis.annotations.*;

@Mapper
public interface LoginTicketMapper {

    //增加一条凭证
    //@Insert({"insert into login_ticket(user_id,ticket,status,expired) ",
    //         "values(#{userId},#{ticket},#{status},#{expired})"
    //        })
   // @Options(useGeneratedKeys = true,keyProperty = "id")//主键自动生成，并指明生成主键注入给哪个属性
    public int insertLoginTicket(LoginTicket loginTicket);

    //根据ticket查询LoginTicket
    //@Select("select id,user_id,ticket,status,expired from login_ticket where ticket=#{ticket}")
    public LoginTicket selectByTicket(String ticket);

    //修改登陆凭证状态
   // @Update("update set login_ticket status=#{status} where ticket=#{ticket}")
    public int updateStataus(String ticket,int status);
}
