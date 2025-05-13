package com.gp.es.service;


import com.gp.es.esMapping.VideoMapping;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;



/**
 * @author 
 */
public interface VideoInfoRepository extends ElasticsearchRepository<VideoMapping, String> {



}
