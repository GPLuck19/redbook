package com.gp.aiActuator.Engine;


import com.gp.aiActuator.entity.GenericSearchResult;
import com.gp.aiActuator.entity.ScorePageItem;
import org.springframework.ai.document.Document;
import org.springframework.stereotype.Service;

import java.util.*;

// 模拟DataClean类
@Service
public class DataClean {

    public List<Document> getData(GenericSearchResult searchResp) {
        List<Document> documents = new ArrayList<>();
        List<ScorePageItem> scorePageItems = searchResp.getResults();

        for (ScorePageItem item : scorePageItems) {
            // 创建一个Map来存储元数据
            Map<String, Object> metadata = new HashMap<>();
            metadata.put("engine", item.getEngine());
            metadata.put("template", item.getTemplate());
            metadata.put("category", item.getCategory());
            metadata.put("url", item.getUrl());
            metadata.put("publishedDate", new Date());
            metadata.put("score", item.getScore());
            metadata.put("title", item.getTitle());

            // 创建Document对象并添加到列表中
            Document doc = new Document.Builder()
                    .text(item.getContent())
                    .metadata(metadata)
                    .build();

            documents.add(doc);
        }
        return documents;
    }


    // 模拟限制结果数量的方法
    public List<Document> limitResults(List<Document> data, int maxResults) {
        if (data.size() <= maxResults) {
            return data;
        }
        return data.subList(0, maxResults);
    }
}