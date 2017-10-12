package com.demo.service;

/**
 * Created by tracy  on 2017/7/13.
 */

import com.querydsl.core.types.dsl.BooleanExpression;
import com.demo.domain.mongo.info;
import com.demo.domain.mongo.info.QInfo;
import com.demo.repository.mongo.InfoRepository;
import com.demo.service.mapper.InfoMapper;
import com.demo.web.errors.ErrorConstants;
import com.demo.web.errors.MicroserviceClientException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;
import java.util.stream.StreamSupport;

@Service
@Transactional
public class PolicyService {
    @Autowired
    private InfoRepository infoRepository;
    @Autowired
    private infoMapper infoMapper;

    public Info saveInfo(Info info){return infoRepository.save(info);}

    @Transactional(readOnly = true)
    public Optional<Info> findOne(String id){
        return Optional.ofNullable(infoRepository.findOne(id));
    }

    @Transactional(readOnly = true)
    public Optional<Info> findOneByFlowId(String flowId){
        Optional.ofNullable(flowId)
            .orElseThrow(() -> new MicroserviceClientException(ErrorConstants.ERR_QUERY_ERROR, “invalid flow id”));
        BooleanExpression booleanExpression = QInfo.info.flowId.eq(flowId);
        return Optional.ofNullable(infoRepository.findOne(booleanExpression));
    }

   
}
