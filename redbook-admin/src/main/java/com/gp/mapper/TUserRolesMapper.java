package com.gp.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gp.entity.TUserRoles;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
* @author Administrator
* @description 针对表【t_user_roles(用户角色关联表，管理用户与角色之间的多对多关系)】的数据库操作Mapper
* @createDate 2024-12-03 10:58:43
* @Entity com.gp.entity.TUserRoles
*/
public interface TUserRolesMapper extends BaseMapper<TUserRoles> {

    @Select("SELECT role_id FROM t_user_roles WHERE user_id = #{userId} AND del_flag = 0")
    List<Long> getRoleIdsByUserId(Long userId);

    // 删除用户的所有角色关联
    @Delete("DELETE FROM t_user_roles WHERE user_id = #{userId}")
    void deleteRolesByUserId(@Param("userId") Long userId);

    // 批量插入用户角色关联
    @Insert("<script>" +
            "INSERT INTO t_user_roles (user_id, role_id, create_time, update_time, del_flag) VALUES " +
            "<foreach collection='userRoles' item='userRole' separator=','>" +
            "(#{userRole.userId}, #{userRole.roleId}, NOW(), NOW(), 0)" +
            "</foreach>" +
            "</script>")
    void insertUserRoles(@Param("userRoles") List<TUserRoles> userRoles);

}




