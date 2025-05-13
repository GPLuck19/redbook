package com.gp.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gp.dto.req.user.MomentDao;
import com.gp.dto.req.user.MomentListDao;
import com.gp.dto.resp.user.FriendVo;
import com.gp.dto.resp.user.MomentVo;
import com.gp.dto.resp.user.UserInfo;
import com.gp.entity.TMoments;
import com.gp.mapper.TMomentCommentsMapper;
import com.gp.mapper.TUserFriendMapper;
import com.gp.page.PageResponse;
import com.gp.result.Result;
import com.gp.result.Results;
import com.gp.service.TLikesService;
import com.gp.service.TMomentCommentsService;
import com.gp.service.TMomentsService;
import com.gp.mapper.TMomentsMapper;
import com.gp.service.TUserService;
import com.gp.utils.PageUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
* @author Administrator
* @description 针对表【t_moments(朋友圈动态表)】的数据库操作Service实现
* @createDate 2025-01-06 14:06:50
*/
@Service
@RequiredArgsConstructor
public class TMomentsServiceImpl extends ServiceImpl<TMomentsMapper, TMoments>
    implements TMomentsService{

    private final TUserFriendMapper userFriendMapper;

    private final TMomentsMapper momentsMapper;

    private final TUserService userService;

    private final TLikesService likesService;

    private final TMomentCommentsService momentCommentsService;

    @Override
    public PageResponse<MomentVo> getUserMoments(MomentListDao requestParam) {
        List<FriendVo> friendList = userFriendMapper.getFriendList(requestParam.getUserId(), 1, null);
        List<Long> friendIds  = friendList.stream().map(FriendVo::getFriendId).collect(Collectors.toList());
        friendIds.add(requestParam.getUserId());
        LambdaQueryWrapper<TMoments> queryWrapper = Wrappers.lambdaQuery(TMoments.class)
                .orderByDesc(TMoments::getCreateTime);
        if(ObjectUtil.isNotNull(friendIds)){
            queryWrapper.in(TMoments::getUserId, friendIds);
        }
        IPage<TMoments> momentPage = momentsMapper.selectPage(PageUtil.convert(requestParam), queryWrapper);
        return PageUtil.convert(momentPage, each -> {
            UserInfo userInfo = userService.getUserInfoById(each.getUserId());
            MomentVo momentVo = BeanUtil.toBean(each, MomentVo.class);
            momentVo.setRealName(userInfo.getRealName());
            momentVo.setDateTime(each.getCreateTime());
            momentVo.setUserPic(userInfo.getUserPic());
            momentVo.setCommentVoList(momentCommentsService.getCommentsBymomentId(each.getId()));
            return momentVo;
        });
    }

    @Override
    public Result publishMoment(MomentDao requestParam) {
        TMoments moments = TMoments.builder().userId(requestParam.getUserId())
                .content(requestParam.getContent())
                .images(requestParam.getImages().toString())
                .location(requestParam.getLocation()).build();
        int count = momentsMapper.insert(moments);
        if(count>0){
            return Results.success();
        }
        return Results.failure();
    }

    @Override
    @Transactional
    public Result removeMoment(Long momentId) {
        this.removeById(momentId);
        likesService.deleteLikeById(momentId);
        momentCommentsService.deleteMomentCommentsById(momentId);
        return null;
    }
}




