package com.gp.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.gp.base.BaseDO;
import lombok.Data;

import java.util.Date;

/**
 * 用户观看记录表
 * @TableName t_video_watch_log
 */
@TableName(value ="t_video_watch_log")
@Data
public class TVideoWatchLog extends BaseDO {
    /**
     * 观看记录 ID
     */
    @TableId(value = "log_id", type = IdType.AUTO)
    private Long logId;

    /**
     * 用户 ID
     */
    @TableField(value = "user_id")
    private Long userId;

    /**
     * 视频 ID
     */
    @TableField(value = "video_id")
    private Long videoId;

    /**
     * 观看时间
     */
    @TableField(value = "watch_time")
    private Date watchTime;



    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}