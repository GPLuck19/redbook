package com.gp.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.gp.entity.TObjTags;

import java.util.List;

/**
* @author Administrator
* @description 针对表【t_post_tags(文章标签关联表)】的数据库操作Service
* @createDate 2024-11-21 09:50:16
*/
public interface TObjTagsService extends IService<TObjTags> {

    void addPostTag(Long objId, List<Long> tagList,Integer type);

    Boolean deletePostTagById(Long objId,Integer type);

}
