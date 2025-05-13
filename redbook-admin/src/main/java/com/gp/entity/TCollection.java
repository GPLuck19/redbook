package com.gp.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.gp.base.BaseDO;
import lombok.Builder;
import lombok.Data;

/**
 * 收藏表
 * @TableName t_post_collection
 */
@TableName(value ="t_collection")
@Data
@Builder
public class TCollection extends BaseDO {
    /**
     * 关联 ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 对象 ID
     */
    @TableField(value = "obj_id")
    private Long objId;

    /**
     * 用户 ID
     */
    @TableField(value = "user_id")
    private Long userId;

    /**
     * 1.文章  2，视频
     */
    @TableField(value = "collection_type")
    private Integer collectionType;



    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}