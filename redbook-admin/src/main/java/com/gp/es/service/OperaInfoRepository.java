package com.gp.es.service;


import com.gp.es.esMapping.OperaLogsMapping;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;


/**
 * @author 
 */
public interface OperaInfoRepository extends ElasticsearchRepository<OperaLogsMapping, String> {



}
