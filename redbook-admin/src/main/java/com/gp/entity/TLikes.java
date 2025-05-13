package com.gp.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.gp.base.BaseDO;
import lombok.Builder;
import lombok.Data;

/**
 * 点赞表
 * @TableName t_likes
 */
@TableName(value ="t_likes")
@Data
@Builder
public class TLikes extends BaseDO {
    /**
     * 点赞 ID
     */
    @TableId(value = "like_id", type = IdType.AUTO)
    private Long likeId;

    /**
     * 对象 ID
     */
    @TableField(value = "obj_id")
    private Long objId;

    /**
     * 点赞用户 ID
     */
    @TableField(value = "user_id")
    private Long userId;

    /**
     * 1.文章  2，视频 3.朋友圈
     */
    @TableField(value = "like_type")
    private Integer likeType;



    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}