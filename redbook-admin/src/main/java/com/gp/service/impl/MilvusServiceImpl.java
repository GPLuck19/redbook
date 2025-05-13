package com.gp.service.impl;


import com.alibaba.fastjson2.JSONObject;
import com.gp.aiActuator.entity.MilvusArchive;
import com.gp.service.MilvusService;
import io.milvus.client.MilvusServiceClient;
import io.milvus.common.clientenum.ConsistencyLevelEnum;
import io.milvus.grpc.SearchResults;
import io.milvus.param.IndexType;
import io.milvus.param.MetricType;
import io.milvus.param.R;
import io.milvus.param.collection.CollectionSchemaParam;
import io.milvus.param.collection.CreateCollectionParam;
import io.milvus.param.collection.FieldType;
import io.milvus.param.collection.HasCollectionParam;
import io.milvus.param.dml.InsertParam;
import io.milvus.param.dml.SearchParam;
import io.milvus.param.index.CreateIndexParam;
import io.milvus.response.QueryResultsWrapper;
import io.milvus.response.SearchResultsWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.milvus.MilvusVectorStore;
import org.springframework.stereotype.Service;

import java.util.*;


/**
 * @des MilvusServiceImpl
 * @date 2025/2/25 下午3:10
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class MilvusServiceImpl implements MilvusService {

    private final EmbeddingModel embeddingModel;

    private final MilvusVectorStore milvusClient;

    @Override
    public void embedding(Document document,String kid) {
        MilvusServiceClient milvusServiceClient = (MilvusServiceClient) milvusClient.getNativeClient().get();
        // 准备插入 Milvus 的数据
        List<InsertParam.Field> fields = new ArrayList<>();
        fields.add(InsertParam.Field.builder().name(MilvusArchive.Field.ID).values(Collections.singletonList(document.getId())).build());
        fields.add(InsertParam.Field.builder().name(MilvusArchive.Field.TEXT).values(Collections.singletonList(document.getText())).build());
        fields.add(InsertParam.Field.builder().name(MilvusArchive.Field.METADATA).values(Collections.singletonList(new JSONObject().fluentPut("kid", kid))).build());
        fields.add(InsertParam.Field.builder().name(MilvusArchive.Field.FILE_NAME).values(Collections.singletonList(new Date().toString()+"-"+kid)).build());
        List<List<Float>> book_intro_array = new ArrayList<>();
        List<Float> vectorList = new ArrayList<>();
        float[] floatArray = embeddingModel.embed(document.getText());
        for (float f : floatArray) {
            vectorList.add(f);
        }
        book_intro_array.add(vectorList);
        fields.add(InsertParam.Field.builder().name(MilvusArchive.Field.FEATURE).values(book_intro_array).build());
        milvusServiceClient.insert(InsertParam.newBuilder()
                .withDatabaseName(MilvusVectorStore.DEFAULT_DATABASE_NAME)
                .withCollectionName(kid)
                .withFields(fields)
                .build());
    }

    @Override
    public List<Document> search(String query, String kid, int tok) {
        List<Document> documents = new ArrayList<>();
        MilvusServiceClient milvusServiceClient = (MilvusServiceClient) milvusClient.getNativeClient().get();
        List<String> search_output_fields = Arrays.asList(MilvusArchive.Field.ID,MilvusArchive.Field.TEXT);
        List<List<Float>> book_intro_array = new ArrayList<>();
        List<Float> vectorList = new ArrayList<>();
        float[] floatArray = embeddingModel.embed(query);
        for (float f : floatArray) {
            vectorList.add(f);
        }
        book_intro_array.add(vectorList);
        SearchParam searchParam = SearchParam.newBuilder()
                .withCollectionName(kid)
                .withConsistencyLevel(ConsistencyLevelEnum.STRONG)
                .withMetricType(MetricType.L2)
                .withOutFields(search_output_fields)
                .withTopK(tok)
                .withVectors(book_intro_array)
                .withVectorFieldName(MilvusArchive.Field.FEATURE)
                .withParams("{\"nprobe\":10,\"offset\":2, \"limit\":3}")
                .build();
        R<SearchResults> response = milvusServiceClient.search(searchParam);
        SearchResultsWrapper wrapper = new SearchResultsWrapper(response.getData().getResults());
        List<QueryResultsWrapper.RowRecord> records = wrapper.getRowRecords();
        for (QueryResultsWrapper.RowRecord record:records) {
            Map<String, Object> fieldValues = record.getFieldValues();
            Document document = Document.builder().id((String) fieldValues.get(MilvusArchive.Field.ID)).text((String) fieldValues.get(MilvusArchive.Field.TEXT)).build();
            documents.add(document);
        }
        return documents;
    }


    /**
     * 创建集合
     */
    public void createCollection(String kid) {
        MilvusServiceClient milvusServiceClient = (MilvusServiceClient) milvusClient.getNativeClient().get();
        R<Boolean> hasCollection = milvusServiceClient.hasCollection(HasCollectionParam.newBuilder().withDatabaseName(MilvusVectorStore.DEFAULT_DATABASE_NAME).withCollectionName(kid).build());
        if (hasCollection.getData()) {
            log.info("集合已经存在");
            return;
        }

        CollectionSchemaParam schema = CollectionSchemaParam.newBuilder()
                .addFieldType(FieldType.newBuilder()
                .withName(MilvusArchive.Field.ID)
                .withMaxLength(100)
                .withDataType(io.milvus.grpc.DataType.VarChar)
                .withPrimaryKey(true)
                .withAutoID(true)
                .build())
                .addFieldType(FieldType.newBuilder()
                .withName(MilvusArchive.Field.FILE_NAME)
                .withDescription("文件名")
                .withDataType(io.milvus.grpc.DataType.VarChar)
                .build())
                .addFieldType(FieldType.newBuilder()
                .withName(MilvusArchive.Field.FEATURE)
                .withDescription("特征向量")
                .withDataType(io.milvus.grpc.DataType.FloatVector)
                // 设置向量维度
                .withDimension(MilvusArchive.FEATURE_DIM)
                .build())
                .addFieldType(FieldType.newBuilder()
                .withName(MilvusArchive.Field.TEXT)
                .withDescription("文本")
                .withDataType(io.milvus.grpc.DataType.VarChar)
                .build())
                .addFieldType(FieldType.newBuilder()
                .withName(MilvusArchive.Field.METADATA)
                .withDescription("元数据")
                .withDataType(io.milvus.grpc.DataType.VarChar)
                .build()).build();

        CreateCollectionParam createCollectionParam = CreateCollectionParam.newBuilder()
                .withCollectionName(kid)
                .withSchema(schema)
                .withDatabaseName(MilvusVectorStore.DEFAULT_DATABASE_NAME)
                .build();

        milvusServiceClient.createCollection(createCollectionParam);

        CreateIndexParam createIndexParam = CreateIndexParam.newBuilder()
                .withDatabaseName(MilvusVectorStore.DEFAULT_DATABASE_NAME)
                .withCollectionName(kid)
                .withFieldName(MilvusArchive.Field.FEATURE)
                .withMetricType(MetricType.COSINE)
                .withIndexType(IndexType.IVF_FLAT)
                .build();

        milvusServiceClient.createIndex(createIndexParam);
    }
}