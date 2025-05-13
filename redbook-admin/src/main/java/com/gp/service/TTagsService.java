package com.gp.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.gp.entity.TTags;

import java.util.List;

/**
* @author Administrator
* @description 针对表【t_tags(标签表)】的数据库操作Service
* @createDate 2024-11-21 09:50:32
*/
public interface TTagsService extends IService<TTags> {

    List<TTags> findTagList();

}
