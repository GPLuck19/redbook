package com.gp.dto.resp.content;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VideoVo {

    private Long videoId;

    private String videoUrl;

    private String coverImage;

    private String title;

    private Integer duration;

    private Boolean isLike;

    private Boolean isCollection;

    private String username;

    private String avatar;

    private Long userId;

    private Integer likeCount;

    private Integer collectionCount;

    private Integer status;

}
