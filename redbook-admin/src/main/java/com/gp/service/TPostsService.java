package com.gp.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.gp.dto.req.content.AddPostDao;
import com.gp.dto.req.content.PostListDao;
import com.gp.dto.resp.content.PostListVo;
import com.gp.entity.TPosts;
import com.gp.page.PageResponse;
import com.gp.result.Result;

/**
* @author Administrator
* @description 针对表【t_posts(文章表)】的数据库操作Service
* @createDate 2024-11-21 09:50:24
*/
public interface TPostsService extends IService<TPosts> {

    PageResponse<PostListVo> findPostList(PostListDao requestParam);

    Result removePost(Long postId);

    Result addPost(AddPostDao requestParam);

    Result updatePost(AddPostDao requestParam);

    PostListVo findPostById(Long postId);

    PageResponse<PostListVo> findPostInfo(PostListDao requestParam);
}
