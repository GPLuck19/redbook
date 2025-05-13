package com.gp.dto.resp.user;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.gp.dto.resp.content.CommentVo;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Data
public class MomentVo {
    /**
     * 动态ID
     */
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 动态内容
     */
    private String content;

    /**
     * 图片链接，多个用逗号分隔
     */
    private String images;


    /**
     * 地理位置
     */
    private String location;

    /**
     * 真实姓名
     */
    private String realName;

    /**
     * 发布时间
     */
    private Date dateTime;

    /**
     * 用户头像
     */
    private String userPic;

    private List<MomentCommentVo> commentVoList;
}
