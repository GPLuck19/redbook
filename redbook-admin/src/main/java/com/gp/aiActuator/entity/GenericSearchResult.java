package com.gp.aiActuator.entity;



import com.gp.aiActuator.Engine.NameInMap;

import java.util.List;
import java.util.Map;
 
public class GenericSearchResult extends TeaModel {
	@NameInMap("results")
	public List<ScorePageItem> results;
 
	@NameInMap("query")
	public String query;
 
	@NameInMap("number_of_results")
	public Double numberOfResults;
 
	public GenericSearchResult() {
	}
 
	public static GenericSearchResult build(Map<String, ?> map) throws Exception {
		GenericSearchResult self = new GenericSearchResult();
		return (GenericSearchResult) TeaModel.build(map, self);
	}
 
	public List<ScorePageItem> getResults() {
		return results;
	}
 
	public GenericSearchResult setResults(List<ScorePageItem> results) {
		this.results = results;
		return this;
	}
 
	public String getQuery() {
		return query;
	}
 
	public GenericSearchResult setQuery(String query) {
		this.query = query;
		return this;
	}

	public Double getNumberOfResults() {
		return numberOfResults;
	}

	public void setNumberOfResults(Double numberOfResults) {
		this.numberOfResults = numberOfResults;
	}
}