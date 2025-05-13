package com.gp.aiActuator.entity;

/**
 * @des Milvus 向量数据库业务参数
 * @date 2025/2/25 下午2:54
 */
public class MilvusArchive {
    /**
     * 向量数据库名称
     */
    public static final String DB_NAME = "default";

    /**
     * 分片数量
     */
    public static final int SHARDS_NUM = 1;
    /**
     * 分区数量
     */
    public static final int PARTITION_NUM = 1;
    /**
     * 特征向量维度
     */
    public static final Integer FEATURE_DIM = 1536;
    /**
     * 字段
     */
    public static class Field {
        /**
         * id
         */
        public static final String ID = "id";
        /**
         * 文本特征向量
         */
        public static final String FEATURE = "feature";
        /**
         * 文本
         */
        public static final String TEXT = "text";
        /**
         * 文件名
         */
        public static final String FILE_NAME = "file_name";
        /**
         * 元数据
         */
        public static final String METADATA = "metadata";
    }
}