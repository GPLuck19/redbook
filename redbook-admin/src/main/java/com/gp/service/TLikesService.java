package com.gp.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.gp.entity.TLikes;

import java.util.List;

/**
* @author Administrator
* @description 针对表【t_likes(点赞表)】的数据库操作Service
* @createDate 2024-11-21 09:50:03
*/
public interface TLikesService extends IService<TLikes> {

    Boolean deleteLikeById(Long postId);

    List<TLikes> findLikeList(Long userId, int likeType);

    void deleteLikeByUserIdAndPostId(Long userId, Long objId);
}
