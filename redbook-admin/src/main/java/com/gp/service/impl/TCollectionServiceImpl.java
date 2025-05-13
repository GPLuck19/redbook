package com.gp.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gp.entity.TCollection;
import com.gp.mapper.TPostCollectionMapper;
import com.gp.service.TCollectionService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
* @author Administrator
* @description 针对表【t_post_collection(文章收藏表)】的数据库操作Service实现
* @createDate 2024-11-26 12:51:58
*/
@Service
public class TCollectionServiceImpl extends ServiceImpl<TPostCollectionMapper, TCollection>
    implements TCollectionService {

    @Override
    public Boolean deletePostCollectionById(Long postId) {
        LambdaQueryWrapper<TCollection> deleteWrapper = Wrappers.lambdaQuery(TCollection.class)
                .eq(TCollection::getObjId,postId);
        return this.remove(deleteWrapper);
    }

    @Override
    public List<TCollection> findCollectionList(Long userId,int collectionType) {
        LambdaQueryWrapper<TCollection> wrapper = Wrappers.lambdaQuery(TCollection.class)
                .eq(TCollection::getUserId,userId)
                .eq(TCollection::getCollectionType,collectionType);
        return this.list(wrapper);
    }

    @Override
    public void deleteCollectByUserIdAndPostId(Long userId, Long objId) {
        LambdaQueryWrapper<TCollection> wrapper = Wrappers.lambdaQuery(TCollection.class).eq(TCollection::getUserId, userId).eq(TCollection::getObjId, objId);
        this.remove(wrapper);
    }
}




