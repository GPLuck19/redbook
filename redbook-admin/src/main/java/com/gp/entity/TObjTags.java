package com.gp.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.gp.base.BaseDO;
import lombok.Builder;
import lombok.Data;

/**
 * 文章标签关联表
 * @TableName t_post_tags
 */
@TableName(value ="t_obj_tags")
@Data
@Builder
public class TObjTags extends BaseDO {
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
     * 标签 ID
     */
    @TableField(value = "tag_id")
    private Long tagId;

    /**
     * 标签 类型 1.文章  2，商品
     */
    @TableField(value = "type")
    private int type;



    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}