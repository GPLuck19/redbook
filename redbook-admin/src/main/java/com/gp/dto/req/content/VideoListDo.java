package com.gp.dto.req.content;

import com.gp.page.PageRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VideoListDo extends PageRequest {


    /**
     * 视频标题
     */
    private String title;


    /**
     * 上传用户的用户名
     */
    private String username;


    private Long userId;

    private Boolean isCollection;

    private Boolean isLike;


    /**
     * 审核状态 0.审核中 1.审核通过 2.审核不通过
     */
    private Integer status;

}
