package com.gp.taskJob;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.gp.entity.TPosts;
import com.gp.mapper.TPostsMapper;
import com.gp.service.TObjTagsService;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ThirdPartyJob {
    private static Logger logger = LoggerFactory.getLogger(ThirdPartyJob.class);

    private final TPostsMapper postsMapper;

    private final TObjTagsService objTagsService;

    private final ChatClient chatClient;

    private static final String API_KEY = "96f163cda80b";  // API 密钥
    private static final String BASE_URL = "https://whyta.cn/api/tx/generalnews";  // 接口地址


    /**
     * 获取第三方文章信息同步本地数据库
     */
    @XxlJob("AddThirdPartyPostJobHandler")
    public void AddThirdPartyPostJobHandler() throws Exception {
        // 构建请求 URL
        String url = UriComponentsBuilder.fromHttpUrl(BASE_URL)
                .queryParam("key", API_KEY)    // API 密钥
                .queryParam("num", 10)    // 数量
                .toUriString();

        // 创建 RestTemplate 实例
        RestTemplate restTemplate = new RestTemplate();
        try {
            // 发送 GET 请求
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            String jsonResponse = response.getBody();
            JSONObject jsonObject = JSONObject.parseObject(jsonResponse);
            if(jsonObject.getInteger("code")==200){
                JSONArray array = jsonObject.getJSONObject("result").getJSONArray("newslist");
                array.stream().forEach(item -> {
                    JSONObject json = (JSONObject) item;
                    List<String> images=new ArrayList<>();
                    images.add(json.getString("picUrl"));
                    //ai加强内容
                    String call = chatClient.prompt().user("结合标题"+json.getString("title")+"和链接"+json.getString("url")+"编写一段内容，字数在300字左右，你的回答不需要带有提示词，类似于好的，以下是为您润色后的 200 字左右信息，直接回复润色后的内容即可！").call().content();
                    TPosts posts = TPosts.builder().image(images.toString())
                            .title(json.getString("title"))
                            .content(call)
                            .username(json.getString("source"))
                            .userId(1L).build();
                    posts.setCreateTime(json.getDate("ctime"));
                    posts.setCreateTime(new Date());
                    postsMapper.insert(posts);
                    List<Long> tagList=new ArrayList<>();
                    tagList.add(8L);
                    objTagsService.addPostTag(posts.getPostId(),tagList,1);
                });
            }


        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error occurred while fetching news.");
        }
    }



    public void init(){
        logger.info("init");
    }
    public void destroy(){
        logger.info("destroy");
    }


}
