package com.gp.es.service;


import com.gp.es.esMapping.GoodsMapping;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;


/**
 * @author 
 */
public interface GoodsInfoRepository extends ElasticsearchRepository<GoodsMapping, String> {



}
