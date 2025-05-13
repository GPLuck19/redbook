package com.gp.es.esMapping;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(indexName = "video")
public class VideoMapping {

    @Id
    private Long videoId;


    private String userName;


    private Long userId;

    /**
     * 视频标题
     */
    private String title;

    /**
     * 视频链接
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

    @Field(type = FieldType.Date, format = DateFormat.date_optional_time)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;


    private Integer likeCount;

    private Integer collectionCount;



}
