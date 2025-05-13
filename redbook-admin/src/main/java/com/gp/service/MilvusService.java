package com.gp.service;

import org.springframework.ai.document.Document;

import java.util.List;

/**
 * @des Milvus服务接口
 * @date 2025/2/25 下午3:09
 */
public interface MilvusService {

    void embedding(Document document,String kid);

    List<Document> search(String query, String kid, int tok);

    void createCollection(String kid);
}