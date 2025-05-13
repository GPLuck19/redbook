package com.gp.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gp.entity.TTags;
import com.gp.mapper.TTagsMapper;
import com.gp.service.TTagsService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
* @author Administrator
* @description 针对表【t_tags(标签表)】的数据库操作Service实现
* @createDate 2024-11-21 09:50:32
*/
@Service
public class TTagsServiceImpl extends ServiceImpl<TTagsMapper, TTags>
    implements TTagsService{


    @Override
    public List<TTags> findTagList() {
        return this.list();
    }
}




