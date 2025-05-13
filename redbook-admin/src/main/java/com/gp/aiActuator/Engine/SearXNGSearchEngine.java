package com.gp.aiActuator.Engine;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.gp.aiActuator.entity.GenericSearchResult;
import com.gp.aiActuator.entity.ScorePageItem;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
@Service
public class SearXNGSearchEngine {

    // 模拟搜索方法，这里简单返回一个包含搜索结果的GenericSearchResult对象
    public GenericSearchResult search(String query) {
        GenericSearchResult result = new GenericSearchResult();
        try {
            String encodedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8.toString());

            // 构建请求参数
            String url =  "http://ip:9380/search?q=" + encodedQuery + "&format=json&engines=bing";

            // 创建HttpClient和HttpRequest
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Content-Type", "application/json")
                    .GET()
                    .build();

            // 发送请求并获取响应
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            Gson gson = new Gson();
            // 解析响应数据
            Map<String, Object> responseMap = gson.fromJson(response.body(), new TypeToken<Map<String, Object>>() {}.getType());
            result.setNumberOfResults((Double) responseMap.get("number_of_results"));
            List<Map<String, Object>> resultsList = (List<Map<String, Object>>) responseMap.get("results");
            List<ScorePageItem> scorePageItems = gson.fromJson(gson.toJson(resultsList), new TypeToken<List<ScorePageItem>>() {}.getType());
            result.setResults(scorePageItems);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

        return result;
    }
}