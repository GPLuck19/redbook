package com.gp.controller.ai;

import com.gp.service.MilvusService;
import lombok.SneakyThrows;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.core.io.InputStreamResource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
public class KnowledgeController {

    private final MilvusService milvusService;

    private final VectorStore vectorStore;


    public KnowledgeController(
            MilvusService milvusService,
            VectorStore vectorStore
    ) {
        this.milvusService = milvusService;
        this.vectorStore = vectorStore;
    }


    /**
     * 嵌入文件
     *
     * @param file 待嵌入的文件
     * @return 是否成功
     */
    @SneakyThrows
    @PostMapping("/api/ai/embedding")
    public Boolean embedding(@RequestParam("file") MultipartFile file,@RequestParam("kid") String kid) {
        // 存入向量数据库，这个过程会自动调用embeddingModel,将文本变成向量再存入。
        TikaDocumentReader tikaDocumentReader = new TikaDocumentReader(new InputStreamResource(file.getInputStream()));
        // 将文本内容划分成更小的块
        List<Document> documents = new TokenTextSplitter()
                .apply(tikaDocumentReader.read());
        documents.forEach(document -> {
            document.getMetadata().put("docId", kid);
        });
        vectorStore.add(documents);
        return true;
    }

    /**
     * 知识库个人搜索
     * @return 是否成功
     */
    @PostMapping("/api/ai/searchK")
    public List<Document> searchK(@RequestParam("kid") String kid, @RequestParam("query") String query, @RequestParam("tok") int tok) {
        return milvusService.search(query, kid, tok);
    }

    /**
     * 知识库个人向量嵌入
     * @return 是否成功
     */
    @SneakyThrows
    @PostMapping("/api/ai/embeddingK")
    public Boolean embeddingK(@RequestParam("kid") String kid,@RequestParam("file") MultipartFile file) {
        TikaDocumentReader tikaDocumentReader = new TikaDocumentReader(new InputStreamResource(file.getInputStream()));
        // 将文本内容划分成更小的块
        List<Document> documents = new TokenTextSplitter()
                .apply(tikaDocumentReader.read());
        documents.forEach(document -> {
            milvusService.embedding(document, kid);
        });
        return true;
    }

}
