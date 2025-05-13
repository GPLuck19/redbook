package com.gp.es.service;


import com.gp.es.esMapping.LoginLogsMapping;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;


/**
 * @author 
 */
public interface LoginInfoRepository extends ElasticsearchRepository<LoginLogsMapping, String> {



}
