package com.gp.aiActuator.PreRetrieval;

import com.gp.aiActuator.Engine.DataClean;
import com.gp.aiActuator.entity.GenericSearchResult;
import com.gp.aiActuator.Engine.SearXNGSearchEngine;
import org.slf4j.Logger;
import javax.validation.constraints.NotNull;
import org.slf4j.LoggerFactory;
import org.springframework.ai.document.Document;
import org.springframework.ai.rag.Query;
import org.springframework.ai.rag.postretrieval.ranking.DocumentRanker;
import org.springframework.ai.rag.retrieval.search.DocumentRetriever;
import org.springframework.lang.Nullable;

import java.util.List;
 

 /*WebSearchRetriever 类通过 searXNG 搜索引擎进行网页搜索，对搜索结果进行数据清洗和数量限制，
 并根据配置决定是否对文档进行排序，最终返回相关的文档列表。它提供了使用建造者模式进行自定义配置的功能。*/
public class WebSearchRetriever implements DocumentRetriever {
 
	private static final Logger logger = LoggerFactory.getLogger(WebSearchRetriever.class);
	//使用 searXNG
 
	private final SearXNGSearchEngine searchEngine;
 
	private final int maxResults;
 
	private final DataClean dataCleaner;
 
	private final DocumentRanker documentRanker;
 
	private final boolean enableRanker;
 
 
	private WebSearchRetriever(Builder builder) {
 
		this.searchEngine = builder.searchEngine;
		this.maxResults = builder.maxResults;
		this.dataCleaner = builder.dataCleaner;
		this.documentRanker = builder.documentRanker;
		this.enableRanker = builder.enableRanker;
 
	}
 
	@NotNull
	@Override
	public List<Document> retrieve(
			@Nullable Query query
	) {
 
		// 搜索
		GenericSearchResult searchResp = searchEngine.search(query.text());
 
		// 清洗数据
		List<Document> cleanerData = dataCleaner.getData(searchResp);
		logger.debug("cleaner data: {}", cleanerData);
 
		// 返回结果
		List<Document> documents = dataCleaner.limitResults(cleanerData, maxResults);
		logger.debug("WebSearchRetriever#retrieve() document size: {}, raw documents: {}",
				documents.size(),
				documents.stream().map(Document::getId).toArray()
		);
		return enableRanker ? ranking(query, documents) : documents;
	}
 
	private List<Document> ranking(Query query, List<Document> documents) {
 
		if (documents.size() <= 1) {
			// 只有一个时，不需要 rank
			return documents;
		}
 
		try {
 
			List<Document> rankedDocuments = documentRanker.rank(query, documents);
			logger.debug("WebSearchRetriever#ranking() Ranked documents: {}", rankedDocuments.stream().map(Document::getId).toArray());
			return rankedDocuments;
		} catch (Exception e) {
			// 降级返回原始结果
			logger.error("ranking error", e);
			return documents;
		}
	}
 
	public static Builder builder() {
		return new Builder();
	}
 
 
	public static final class Builder {
 
		private SearXNGSearchEngine searchEngine;
 
		private int maxResults;
 
		private DataClean dataCleaner;
 
		private DocumentRanker documentRanker;
 
		// 默认开启 ranking
		private Boolean enableRanker = true;
 
		public Builder searchEngine(SearXNGSearchEngine searchEngine) {
			this.searchEngine = searchEngine;
			return this;
		}
 
		public Builder dataCleaner(DataClean dataCleaner) {
 
			this.dataCleaner = dataCleaner;
			return this;
		}
 
		public Builder maxResults(int maxResults) {
 
			this.maxResults = maxResults;
			return this;
		}
 
		public Builder documentRanker(DocumentRanker documentRanker) {
			this.documentRanker = documentRanker;
			return this;
		}
 
		public Builder enableRanker(Boolean enableRanker) {
			this.enableRanker = enableRanker;
			return this;
		}
 
		public WebSearchRetriever build() {
			return new WebSearchRetriever(this);
		}
	}
 
}