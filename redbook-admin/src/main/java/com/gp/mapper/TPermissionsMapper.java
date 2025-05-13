package com.gp.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gp.entity.TPermissions;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
* @author Administrator
* @description 针对表【t_permissions(权限表，存储系统中所有的权限信息)】的数据库操作Mapper
* @createDate 2024-12-03 10:58:00
* @Entity com.gp.entity.TPermissions
*/
public interface TPermissionsMapper extends BaseMapper<TPermissions> {

    @Select({
            "<script>",
            "SELECT * FROM t_permissions",
            "WHERE id IN",
            "<foreach collection='permissionIds' item='id' open='(' separator=',' close=')'>",
            "#{id}",
            "</foreach>",
            "AND del_flag = 0 AND operation_type IN (1,2)",
            "</script>"
    })
    List<TPermissions> getPermissionsByIds(@Param("permissionIds") List<Long> permissionIds);

}




