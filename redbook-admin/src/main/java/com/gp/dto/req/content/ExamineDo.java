package com.gp.dto.req.content;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExamineDo {

    /**
     * 被审核内容的 ID
     */
    private Long targetId;

    /**
     * 审核员 ID
     */
    private Long reviewerId;

    /**
     * 审核状态：0 待审核，1 通过，2 拒绝
     */
    private Integer status;

    /**
     * 审核拒绝的原因
     */
    private String reason;

    /**
     * 审核拒绝的备注
     */
    private String remark;

    //被审核内容的类型（如1.视频、2.帖子等）
    private String targetType;

}
