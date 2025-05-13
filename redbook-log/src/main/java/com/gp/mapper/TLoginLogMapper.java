package com.gp.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gp.entity.TLoginLog;
import org.apache.ibatis.annotations.Mapper;

/**
* @author Administrator
* @description 针对表【t_login_log(用户登录信息表)】的数据库操作Mapper
* @createDate 2024-12-16 10:11:23
* @Entity com.gp.entity.TLoginLog
*/
@Mapper
public interface TLoginLogMapper extends BaseMapper<TLoginLog> {

}




