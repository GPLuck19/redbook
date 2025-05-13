package com.gp.mapper;

import com.gp.entity.TMoments;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
* @author Administrator
* @description 针对表【t_moments(朋友圈动态表)】的数据库操作Mapper
* @createDate 2025-01-06 14:06:50
* @Entity com.gp.entity.TMoments
*/
public interface TMomentsMapper extends BaseMapper<TMoments> {

    // 根据用户 ID 列表查询朋友圈动态
    @Select({
            "<script>",
            "SELECT * FROM t_moments WHERE user_id IN ",
            "<foreach item='item' index='index' collection='userIds' open='(' separator=',' close=')'>",
            "#{item}",
            "</foreach>",
            "ORDER BY create_time DESC",
            "</script>"
    })
    List<TMoments> selectMomentsByUserIds(@Param("userIds") List<Long> userIds);

}




