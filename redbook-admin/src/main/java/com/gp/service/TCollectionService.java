package com.gp.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.gp.entity.TCollection;

import java.util.List;

/**
* @author Administrator
* @description 针对表【t_post_collection(文章收藏表)】的数据库操作Service
* @createDate 2024-11-26 12:51:58
*/
public interface TCollectionService extends IService<TCollection> {

    Boolean deletePostCollectionById(Long postId);

    List<TCollection> findCollectionList(Long userId,int collectionType);

    void deleteCollectByUserIdAndPostId(Long userId, Long objId);
}
