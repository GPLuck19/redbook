package com.gp.controller.ai;

import com.alibaba.fastjson2.JSONObject;
import com.gp.aiActuator.Engine.DataClean;
import com.gp.aiActuator.Engine.SearXNGSearchEngine;
import com.gp.aiActuator.PreRetrieval.ConcatenationDocumentJoiner;
import com.gp.aiActuator.PreRetrieval.CustomContextQueryAugmenter;
import com.gp.aiActuator.PreRetrieval.WebSearchRetriever;
import com.gp.dto.req.ai.AiMessageInput;
import com.gp.dto.req.ai.AiMessageWrapper;
import com.gp.utils.SpringUtils;
import lombok.SneakyThrows;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.QuestionAnswerAdvisor;
import org.springframework.ai.chat.client.advisor.RetrievalAugmentationAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.model.Media;
import org.springframework.ai.rag.preretrieval.query.expansion.QueryExpander;
import org.springframework.ai.rag.preretrieval.query.transformation.QueryTransformer;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Flux;

import java.util.Map;

@RestController
public class AgentController {

    private final ChatClient chatClient;

    private final SimpleLoggerAdvisor simpleLoggerAdvisor;

    private final QueryTransformer queryTransformer;

    private final QueryExpander queryExpander;

    private final PromptTemplate queryArgumentPromptTemplate;

    private final WebSearchRetriever webSearchRetriever;

    private final MychatMemory chatMemory;
    private final ToolCallbackProvider tools;

    private final VectorStore vectorStore;

    public AgentController(ChatClient.Builder chatClientBuilder,
                           QueryTransformer queryTransformer,
                           QueryExpander queryExpander,
                           SearXNGSearchEngine searchEngine,
                           DataClean dataCleaner,
                           MychatMemory chatMemory,
                           ToolCallbackProvider tools,
                           VectorStore vectorStore,
                           @Qualifier("queryArgumentPromptTemplate") PromptTemplate queryArgumentPromptTemplate) {
        this.queryTransformer = queryTransformer;
        this.queryExpander = queryExpander;
        this.queryArgumentPromptTemplate = queryArgumentPromptTemplate;
        this.chatMemory = chatMemory;
        this.tools = tools;
        this.vectorStore = vectorStore;


        // Build chatClient
        this.chatClient = chatClientBuilder
                .defaultTools(tools)
                .defaultOptions(ChatOptions.builder().model("qwen2.5:0.5b").build())
                .build();

        // 日志
        this.simpleLoggerAdvisor = new SimpleLoggerAdvisor(100);

        this.webSearchRetriever = WebSearchRetriever.builder()
                .searchEngine(searchEngine)
                .dataCleaner(dataCleaner)
                .maxResults(2)
                .enableRanker(true)
                .build();
    }

    @PostMapping(value = "/api/ai/webSearch", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<String>> search(@RequestPart("input") String input, @RequestPart(name="file",required = false) MultipartFile file){
        System.out.println("\n>>> QUESTION: " + input);
        AiMessageWrapper aiMessageWrapper = JSONObject.parseObject(input, AiMessageWrapper.class);
        SpringUtils.context().publishEvent(aiMessageWrapper.getMessage());
        return chatClient.prompt()
                .system(promptSystemSpec -> useFile(promptSystemSpec, file))
                .advisors(
                        advisorSpec -> {
                            // 使用历史消息
                            useChatHistory(advisorSpec, aiMessageWrapper.getMessage().getSessionId());
                            // 使用向量数据库
                            useVectorStore(advisorSpec, aiMessageWrapper.getParams().getEnableVectorStore());
                            useLogs(advisorSpec);
                            createRetrievalAugmentationAdvisor(advisorSpec);
                        }
                ).user(promptUserSpec -> toPrompt(promptUserSpec, aiMessageWrapper.getMessage()))
                .stream()
                .chatResponse()
                .map(chatResponse -> ServerSentEvent.builder(JSONObject.toJSONString(chatResponse))
                        .event("message")
                        .build());
    }


    private void useChatHistory(ChatClient.AdvisorSpec advisorSpec, String sessionId) {
        advisorSpec.advisors(new MessageChatMemoryAdvisor(chatMemory, sessionId, 10));
    }

    private void useVectorStore(ChatClient.AdvisorSpec advisorSpec, Boolean enableVectorStore) {
        if (!enableVectorStore) return;
        String promptWithContext = """
                下面是上下文信息
                ---------------------
                {question_answer_context}
                ---------------------
                给定的上下文和提供的历史信息，而不是事先的知识，回复用户的意见。如果答案不在上下文中，告诉用户你不能回答这个问题。
                """;
        advisorSpec.advisors(new QuestionAnswerAdvisor(vectorStore, SearchRequest.builder().build(), promptWithContext));
    }

    private void useLogs(ChatClient.AdvisorSpec advisorSpec) {
        advisorSpec.advisors(simpleLoggerAdvisor);
    }


    public void toPrompt(ChatClient.PromptUserSpec promptUserSpec, AiMessageInput input) {
        // AiMessageInput转成Message
        Message message = chatMemory.toSpringAiMessage(input);
        if (message instanceof UserMessage userMessage && !CollectionUtils.isEmpty(userMessage.getMedia())) {
            // 用户发送的图片/语言
            Media[] medias = new Media[userMessage.getMedia().size()];
            promptUserSpec.media(userMessage.getMedia().toArray(medias));
        }
        // 用户发送的文本
        promptUserSpec.text(message.getText());
    }


    @SneakyThrows
    public void useFile(ChatClient.PromptSystemSpec spec, MultipartFile file) {
        if (file == null) return;
        String content = new TikaDocumentReader(new InputStreamResource(file.getInputStream())).get().get(0).getText();
        Message message = new PromptTemplate("""
                已下内容是额外的知识，在你回答问题时可以参考下面的内容
                ---------------------
                {context}
                ---------------------
                """)
                .createMessage(Map.of("context", content));
        spec.text(message.getText());
    }


    private void createRetrievalAugmentationAdvisor(ChatClient.AdvisorSpec advisorSpec) {

        RetrievalAugmentationAdvisor advisor = RetrievalAugmentationAdvisor.builder()
                .documentRetriever(webSearchRetriever)
                .queryTransformers(queryTransformer)
                .queryAugmenter(
                        new CustomContextQueryAugmenter(
                                queryArgumentPromptTemplate,
                                null,
                                true)
                ).queryExpander(queryExpander)
                .documentJoiner(new ConcatenationDocumentJoiner())
                .build();
        advisorSpec.advisors(advisor);
    }

}
