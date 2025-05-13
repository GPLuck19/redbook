package com.gp.service;

import com.gp.dto.req.content.ExamineDo;
import com.gp.entity.TReviews;
import com.baomidou.mybatisplus.extension.service.IService;
import com.gp.result.Result;

/**
* @author Administrator
* @description 针对表【t_reviews(审核表)】的数据库操作Service
* @createDate 2025-01-10 13:40:15
*/
public interface TReviewsService extends IService<TReviews> {


    Result audit(ExamineDo requestParam);
}
