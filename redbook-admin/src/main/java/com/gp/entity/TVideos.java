package com.gp.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.gp.base.BaseDO;
import lombok.Data;

/**
 * 视频表
 * @TableName t_videos
 */
@TableName(value ="t_videos")
@Data
public class TVideos extends BaseDO {
    /**
     * 视频 ID
     */
    @TableId(value = "video_id", type = IdType.AUTO)
    private Long videoId;

    /**
     * 上传视频的用户 ID
     */
    @TableField(value = "user_id")
    private Long userId;

    /**
     * 视频标题
     */
    @TableField(value = "title")
    private String title;

    /**
     * 视频文件的存储路径（如 MinIO 或其他）
     */
    @TableField(value = "video_url")
    private String videoUrl;

    /**
     * 视频封面图路径
     */
    @TableField(value = "cover_image")
    private String coverImage;

    /**
     * 视频时长（秒）
     */
    @TableField(value = "duration")
    private Integer duration;

    /**
     * 视频描述
     */
    @TableField(value = "description")
    private String description;


    /**
     * 上传用户的用户名
     */
    @TableField(value = "username")
    private String username;

    /**
     * 审核状态 0.审核中 1.审核通过 2.审核不通过
     */
    @TableField(value = "status")
    private Integer status;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}