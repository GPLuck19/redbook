package com.gp.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gp.entity.TObjTags;
import com.gp.mapper.TObjTagsMapper;
import com.gp.service.TObjTagsService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
* @author Administrator
* @description 针对表【t_post_tags(文章标签关联表)】的数据库操作Service实现
* @createDate 2024-11-21 09:50:16
*/
@Service
public class TObjTagsServiceImpl extends ServiceImpl<TObjTagsMapper, TObjTags>
    implements TObjTagsService {

    @Override
    public void addPostTag(Long objId, List<Long> tagList,Integer type) {
        tagList.stream().forEach(s->{
            TObjTags postTags = TObjTags.builder().tagId(s).objId(objId).type(type).build();
            this.save(postTags);
        });
    }

    @Override
    public Boolean deletePostTagById(Long objId,Integer type) {
        LambdaQueryWrapper<TObjTags> deleteWrapper = Wrappers.lambdaQuery(TObjTags.class)
                .eq(TObjTags::getObjId,objId).eq(TObjTags::getType,type);
        return this.remove(deleteWrapper);
    }
}




