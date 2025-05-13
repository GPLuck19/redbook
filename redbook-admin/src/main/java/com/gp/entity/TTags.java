package com.gp.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.gp.base.BaseDO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 标签表
 * @TableName t_tags
 */
@TableName(value ="t_tags")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TTags extends BaseDO {
    /**
     * 标签 ID
     */
    @TableId(value = "tag_id", type = IdType.AUTO)
    private Long tagId;

    /**
     * 标签名称
     */
    @TableField(value = "name")
    private String name;



    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}