/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.gp.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gp.domian.LoginLogDao;
import com.gp.domian.OperationLogDao;
import com.gp.entity.TLoginLog;
import com.gp.entity.TOperationLog;
import com.gp.es.esMapping.LoginLogsMapping;
import com.gp.es.esMapping.OperaLogsMapping;
import com.gp.page.PageResponse;
import com.gp.service.TLogService;
import com.gp.utils.PageUtil;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;


/**
 * 日志接口层实现
 */
@Service
@RequiredArgsConstructor
public class TLogServiceImpl implements TLogService {

    private final ElasticsearchTemplate elasticsearchTemplate;


    @Override
    public PageResponse<TLoginLog> findLoginLogs(LoginLogDao requestParam) {
        ArrayList<TLoginLog> arrayList = new ArrayList<>();
        Page page = PageUtil.convert(requestParam);
        int current =(int) page.getCurrent();
        int size = (int) page.getSize();
        Criteria criteria = new Criteria();
        if(StringUtils.isNotEmpty(requestParam.getIpaddr())){
            criteria = criteria.and("ipaddr").contains(requestParam.getIpaddr());
        }
        if(StringUtils.isNotEmpty(requestParam.getUserName())){
            criteria = criteria.and("userName").contains(requestParam.getUserName());
        }
        // 价格范围查询
        Date startTime = requestParam.getStartTime();
        Date stopTime = requestParam.getStopTime();
        if (startTime != null && stopTime != null) {
            criteria = criteria.and("loginTime").between(startTime, stopTime);
        } else if (startTime != null) {
            criteria = criteria.and("loginTime").greaterThanEqual(startTime);
        } else if (stopTime != null) {
            criteria = criteria.and("loginTime").lessThanEqual(stopTime);
        }
        // 分页
        Query query = new CriteriaQuery(criteria)
                .setPageable(PageRequest.of(current - 1, size));
        SearchHits<LoginLogsMapping> searchHits = elasticsearchTemplate.search(query, LoginLogsMapping.class);
        long totalValue = searchHits.getTotalHits();
        for (SearchHit<LoginLogsMapping> searchHit : searchHits.getSearchHits()) {
            LoginLogsMapping account = searchHit.getContent(); // 这就是得到的实体类
            TLoginLog convert = BeanUtil.toBean(account, TLoginLog.class);
            arrayList.add(convert);
        }
        return PageResponse.<TLoginLog>builder()
                .current(page.getCurrent())
                .size(page.getSize())
                .records(arrayList)
                .total(totalValue)
                .build();
    }

    @Override
    public PageResponse<TOperationLog> findOperaLogs(OperationLogDao requestParam) {
        ArrayList<TOperationLog> arrayList = new ArrayList<>();
        Page page = PageUtil.convert(requestParam);
        int current =(int) page.getCurrent();
        int size = (int) page.getSize();
        Criteria criteria = new Criteria();
        if(ObjectUtil.isNotEmpty(requestParam.getOperName())){
            criteria = criteria.and("operName").contains(requestParam.getOperName());
        }
        if(StringUtils.isNotEmpty(requestParam.getTitle())){
            criteria = criteria.and("title").contains(requestParam.getTitle());
        }
        if(ObjectUtil.isNotEmpty(requestParam.getBusinessType())){
            criteria = criteria.and("businessType").is(requestParam.getBusinessType());
        }
        // 价格范围查询
        Date startTime = requestParam.getStartTime();
        Date stopTime = requestParam.getStopTime();
        if (startTime != null && stopTime != null) {
            criteria = criteria.and("operTime").between(startTime, stopTime);
        } else if (startTime != null) {
            criteria = criteria.and("operTime").greaterThanEqual(startTime);
        } else if (stopTime != null) {
            criteria = criteria.and("operTime").lessThanEqual(stopTime);
        }
        // 分页
        Query query = new CriteriaQuery(criteria)
                .setPageable(PageRequest.of(current - 1, size));
        SearchHits<OperaLogsMapping> searchHits = elasticsearchTemplate.search(query, OperaLogsMapping.class);
        long totalValue = searchHits.getTotalHits();
        for (SearchHit<OperaLogsMapping> searchHit : searchHits.getSearchHits()) {
            OperaLogsMapping account = searchHit.getContent(); // 这就是得到的实体类
            TOperationLog convert = BeanUtil.toBean(account, TOperationLog.class);
            arrayList.add(convert);
        }
        return PageResponse.<TOperationLog>builder()
                .current(page.getCurrent())
                .size(page.getSize())
                .records(arrayList)
                .total(totalValue)
                .build();
    }
}
