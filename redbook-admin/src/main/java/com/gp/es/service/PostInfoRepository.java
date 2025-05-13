package com.gp.es.service;


import com.gp.es.esMapping.postMapping;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

/**
 * @author 
 */
public interface PostInfoRepository extends ElasticsearchRepository<postMapping, String> {

    @Query("{ \"bool\": { \"must\": [ { \"terms\": { \"tags\": ?0 } }, { \"match\": { \"title\": \"?1\" } } ] } }")
    List<postMapping> searchByTagsAndTitle(List<String> tags, String title);


}
