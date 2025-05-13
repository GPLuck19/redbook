package com.gp.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gp.dto.resp.user.UserInfo;
import com.gp.entity.TUser;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Set;


/**
* @author Administrator
* @description 针对表【t_user(用户表)】的数据库操作Mapper
* @createDate 2024-10-12 15:00:15
* @Entity com.gp.entity.TUser
*/
public interface TUserMapper extends BaseMapper<TUser> {

    // 批量查询用户信息
    @Select("<script>" +
            "SELECT * FROM t_user WHERE id IN " +
            "<foreach item='userId' collection='userIds' open='(' separator=',' close=')'>" +
            "#{userId}" +
            "</foreach>" +
            "</script>")
    List<UserInfo> selectUserInfoByIds(@Param("userIds") Set<Long> userIds);

}




