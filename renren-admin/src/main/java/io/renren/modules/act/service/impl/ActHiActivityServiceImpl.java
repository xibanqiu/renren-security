package io.renren.modules.act.service.impl;

import io.renren.common.utils.PageUtils;
import io.renren.modules.act.mo.query.ActHiActivityListQuery;
import io.renren.modules.act.mo.query.ActHiActivityPageQuery;
import io.renren.modules.act.service.ActHiActivityService;
import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.HistoryService;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.history.HistoricActivityInstanceQuery;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 *
 * @Date 2020/10/30 15:29
 */
@Slf4j
@Service("actHiActivityService")
public class ActHiActivityServiceImpl implements ActHiActivityService {
    @Autowired
    private HistoryService historyService;

    @Override
    public PageUtils<HistoricActivityInstance> fetchActHisActivityPage(ActHiActivityPageQuery query) {

        HistoricActivityInstanceQuery instanceQuery = this.historyService.createHistoricActivityInstanceQuery();
        instanceQuery.processDefinitionId(query.getProcDefinitionId());
        if (StringUtils.isNotBlank(query.getProcActivityId())) {
            instanceQuery.activityId(query.getProcActivityId());
        }
        if (query.isFinished()) {
            instanceQuery.finished();
        }
        else {
            instanceQuery.unfinished();
        }
        instanceQuery.orderByHistoricActivityInstanceStartTime().desc();
        List<HistoricActivityInstance> instanceList = instanceQuery
                .listPage(query.getLimit() * (query.getPage() - 1), query.getLimit());
        Long count = instanceQuery.count();

        PageUtils<HistoricActivityInstance> pageUtils = new PageUtils(instanceList, count.intValue(), query.getLimit(), query.getPage());

        return pageUtils;
    }


    @Override
    public List<HistoricActivityInstance> fetchActHisActivityList(ActHiActivityListQuery query) {
        HistoricActivityInstanceQuery instanceQuery = this.historyService.createHistoricActivityInstanceQuery();
        instanceQuery.processInstanceId(query.getProcInstanceId());
        if (query.isFinished()) {
            instanceQuery.finished();
        }
        else {
            instanceQuery.unfinished();
        }

        return instanceQuery.list();
    }
}
