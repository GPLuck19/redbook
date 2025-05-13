package com.gp.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gp.entity.TLikes;
import com.gp.mapper.TLikesMapper;
import com.gp.service.TLikesService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
* @author Administrator
* @description 针对表【t_likes(点赞表)】的数据库操作Service实现
* @createDate 2024-11-21 09:50:03
*/
@Service
public class TLikesServiceImpl extends ServiceImpl<TLikesMapper, TLikes>
    implements TLikesService{


    @Override
    public Boolean deleteLikeById(Long postId) {
        LambdaQueryWrapper<TLikes> wrapper = Wrappers.lambdaQuery(TLikes.class).eq(TLikes::getObjId, postId);
        return this.remove(wrapper);
    }

    @Override
    public List<TLikes> findLikeList(Long userId, int likeType) {
        LambdaQueryWrapper<TLikes> wrapper = Wrappers.lambdaQuery(TLikes.class)
                .eq(TLikes::getUserId,userId)
                .eq(TLikes::getLikeType,likeType);
        return this.list(wrapper);
    }

    @Override
    public void deleteLikeByUserIdAndPostId(Long userId, Long objId) {
        LambdaQueryWrapper<TLikes> wrapper = Wrappers.lambdaQuery(TLikes.class).eq(TLikes::getUserId, userId).eq(TLikes::getObjId, objId);
        this.remove(wrapper);
    }
}




