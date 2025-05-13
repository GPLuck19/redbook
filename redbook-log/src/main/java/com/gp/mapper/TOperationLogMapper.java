package com.gp.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gp.entity.TOperationLog;
import org.apache.ibatis.annotations.Mapper;

/**
* @author Administrator
* @description 针对表【t_operation_log(操作日志表)】的数据库操作Mapper
* @createDate 2024-12-16 10:11:18
* @Entity com.gp.entity.TOperationLog
*/
@Mapper
public interface TOperationLogMapper extends BaseMapper<TOperationLog> {

}




