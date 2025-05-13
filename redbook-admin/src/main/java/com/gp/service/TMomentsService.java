package com.gp.service;

import com.gp.dto.req.user.MomentDao;
import com.gp.dto.req.user.MomentListDao;
import com.gp.dto.resp.user.MomentVo;
import com.gp.entity.TMoments;
import com.baomidou.mybatisplus.extension.service.IService;
import com.gp.page.PageResponse;
import com.gp.result.Result;

/**
* @author Administrator
* @description 针对表【t_moments(朋友圈动态表)】的数据库操作Service
* @createDate 2025-01-06 14:06:50
*/
public interface TMomentsService extends IService<TMoments> {

    PageResponse<MomentVo> getUserMoments(MomentListDao requestParam);

    Result publishMoment(MomentDao requestParam);

    Result removeMoment(Long momentId);
}
