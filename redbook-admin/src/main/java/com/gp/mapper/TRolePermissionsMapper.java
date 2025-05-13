package com.gp.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gp.entity.TRolePermissions;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
* @author Administrator
* @description 针对表【t_role_permissions(角色权限关联表，用于管理角色与权限之间的关系)】的数据库操作Mapper
* @createDate 2024-12-03 10:58:20
* @Entity com.gp.entity.TRolePermissions
*/
public interface TRolePermissionsMapper extends BaseMapper<TRolePermissions> {

    @Select({
            "<script>",
            "SELECT permission_id FROM t_role_permissions",
            "WHERE role_id IN",
            "<foreach collection='roleIds' item='roleId' open='(' separator=',' close=')'>",
            "#{roleId}",
            "</foreach>",
            "AND del_flag = 0",
            "</script>"
    })
    List<Long> getPermissionIdsByRoleIds(@Param("roleIds") List<Long> roleIds);

    @Select({
            "<script>",
            "SELECT DISTINCT ts.route_path FROM t_permissions ts, t_role_permissions trs",
            "WHERE ts.id=trs.permission_id AND role_id IN",
            "<foreach collection='roleIds' item='roleId' open='(' separator=',' close=')'>",
            "#{roleId}",
            "</foreach>",
            " AND ts.operation_type=3 ",
            "</script>"
    })
    List<String> getPermissionPathByRoleIds(@Param("roleIds") List<Long> roleIds);


    /**
     * 根据角色ID删除权限
     */
    @Delete("DELETE FROM t_role_permissions WHERE role_id = #{roleId}")
    void deleteByRoleId(@Param("roleId") Long roleId);

    /**
     * 批量插入角色权限关系
     */
    @Insert("<script>" +
            "INSERT INTO t_role_permissions (role_id, permission_id, create_time, update_time, del_flag) VALUES " +
            "<foreach item='item' collection='rolePermissions' separator=','>" +
            "(#{item.roleId}, #{item.permissionId}, NOW(), NOW(),0)" +
            "</foreach>" +
            "</script>")
    void batchInsert(@Param("rolePermissions") List<TRolePermissions> rolePermissions);

    /**
     * 查询角色绑定的权限ID
     */
    @Select("SELECT permission_id FROM t_role_permissions WHERE role_id = #{roleId}")
    List<Long> findPermissionIdsByRoleId(@Param("roleId") Long roleId);


    @Delete("<script>" +
            "DELETE FROM t_role_permissions " +
            "WHERE role_id = #{roleId} " +
            "AND permission_id IN " +
            "<foreach item='id' collection='permissionIds' open='(' separator=',' close=')'>" +
            "#{id}" +
            "</foreach>" +
            "</script>")
    void deleteByRoleIdAndPermissionIds(@Param("roleId") Long roleId,
                                        @Param("permissionIds") List<Long> permissionIds);


}




