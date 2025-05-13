package com.gp.es.esMapping;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.*;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(indexName = "goods")
public class GoodsMapping {

    @Id
    private Long id;

    @CompletionField(analyzer="ik_smart",searchAnalyzer="ik_smart")
    private String nameSuggest;  // 用于联想的字段

    /**
     * 商品名称
     */
    @Field(type = FieldType.Text, analyzer = "ik_max_word", searchAnalyzer = "ik_smart")
    private String goodsname;

    /**
     * 标签
     */
    @Field(type = FieldType.Keyword) // 标签存储为keyword类型
    private List<String> tags;

    /**
     * 用户名
     */
    private String username;

    private Long userId;

    /**
     * 大分类
     */
    private String mt;

    /**
     * 大分类名称
     */
    private String mtname;

    /**
     * 小分类
     */
    private String st;

    /**
     * 小分类名称
     */
    private String stname;

    /**
     * 类型
     */
    private String grade;

    /**
     * 商品图片
     */
    private String pic;

    /**
     * 商品描述
     */
    private String description;

    /**
     * 是否上架
     */
    private Integer ispub;

    /**
     * 新价格
     */
    private BigDecimal price;

    /**
     *  老价格
     */
    private BigDecimal priceold;

    /**
     *  库存数量
     */
    private Long number;

    @Field(type = FieldType.Date, format = DateFormat.date_optional_time)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    @Field(type = FieldType.Date, format = DateFormat.date_optional_time)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;


}
