package com.gp.dto.req.content;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VideoDo {


    private Long videoId;

    /**
     * 上传视频的用户 ID
     */
    private Long userId;

    /**
     * 视频标题
     */
    private String title;

    /**
     * 视频文件的存储路径（如 MinIO 或其他）
     */
    private String videoUrl;

    /**
     * 视频封面图路径
     */
    private String coverImage;

    /**
     * 视频时长（秒）
     */
    private Integer duration;

    /**
     * 视频描述
     */
    private String description;


    /**
     * 上传用户的用户名
     */
    private String username;

}
