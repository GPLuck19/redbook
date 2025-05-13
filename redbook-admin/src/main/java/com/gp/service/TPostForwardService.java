package com.gp.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.gp.dto.req.content.ForwardPostDao;
import com.gp.entity.TPostForward;
import com.gp.result.Result;

/**
* @author Administrator
* @description 针对表【t_post_forward(文章转发文章)】的数据库操作Service
* @createDate 2024-12-20 10:06:03
*/
public interface TPostForwardService extends IService<TPostForward> {

    Result forwardPost(ForwardPostDao requestParam);
}
