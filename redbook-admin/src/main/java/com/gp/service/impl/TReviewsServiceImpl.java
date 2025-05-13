package com.gp.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gp.dto.req.content.ExamineDo;
import com.gp.entity.TReviews;
import com.gp.entity.TVideos;
import com.gp.mapper.TVideosMapper;
import com.gp.result.Result;
import com.gp.result.Results;
import com.gp.service.TReviewsService;
import com.gp.mapper.TReviewsMapper;
import com.gp.service.TUserService;
import com.gp.utils.SpringUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
* @author Administrator
* @description 针对表【t_reviews(审核表)】的数据库操作Service实现
* @createDate 2025-01-10 13:40:15
*/
@Service
@RequiredArgsConstructor
public class TReviewsServiceImpl extends ServiceImpl<TReviewsMapper, TReviews>
    implements TReviewsService{

    private final TUserService userService;

    private final TVideosMapper videosMapper;


    @Override
    @Transactional
    public Result audit(ExamineDo requestParam) {
        TReviews reviews = BeanUtil.toBean(requestParam, TReviews.class);
        reviews.setReviewerName(userService.getById(requestParam.getReviewerId()).getRealName());
        this.save(reviews);
        if(requestParam.getTargetType().equals("1")){
            videosMapper.update(null, Wrappers.lambdaUpdate(TVideos.class).eq(TVideos::getVideoId,requestParam.getTargetId()).set(TVideos::getStatus,requestParam.getStatus()));
            TVideos videos = videosMapper.selectById(requestParam.getTargetId());
            SpringUtils.context().publishEvent(videos);
        }
        return Results.success();
    }
}




