package io.renren.modules.act.service.impl;

import io.renren.common.utils.PageUtils;
import io.renren.modules.act.mo.query.ActHiProcInstanceQuery;
import io.renren.modules.act.mo.query.ActHiTaskPageQuery;
import io.renren.modules.act.service.ActHistoricService;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.activiti.engine.HistoryService;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.history.HistoricTaskInstanceQuery;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 *
 * @Date 2020/7/23 18:26
 */
@Service("actHistoricalService")
public class ActHistoricServiceImpl implements ActHistoricService {

    @Autowired
    private HistoryService historyService;

    @Override
    public HistoricProcessInstance fetchHistoricLastProcInstance(ActHiProcInstanceQuery query) {
        List<HistoricProcessInstance> processInstanceList = this.historyService.
                createHistoricProcessInstanceQuery().processInstanceBusinessKey(query.getBusinessKey())
                .orderByProcessInstanceStartTime().desc().list();

        if (CollectionUtils.isEmpty(processInstanceList)) {
            return null;
        }

        return processInstanceList.get(0);
    }


    @Override
    public List<HistoricProcessInstance> fetchHistoricLastProcInstanceList(Set<String> procInstanceBusinessKeySet) {
        List<HistoricProcessInstance> instanceList = Lists.newArrayList();
        for (String key : procInstanceBusinessKeySet) {
            List<HistoricProcessInstance> processInstanceList = this.historyService.createHistoricProcessInstanceQuery().processInstanceBusinessKey(key)
                    .orderByProcessInstanceStartTime().desc().list();

            if (CollectionUtils.isEmpty(processInstanceList)) {
                continue;
            }
            instanceList.add(processInstanceList.get(0));
        }
        return instanceList;
    }

    @Override
    public Map<String, HistoricProcessInstance> fetchHistoricLastProcInstanceMap(Set<String> procInstanceBusinessKeySet) {
        List<HistoricProcessInstance> instanceList = this.fetchHistoricLastProcInstanceList(procInstanceBusinessKeySet);

        return instanceList.stream().collect(Collectors.toMap(HistoricProcessInstance::getBusinessKey, p->p));
    }

    @Override
    public Map<String, List<HistoricProcessInstance>> fetchHistoricLastAllProcInstanceMap(Set<String> procInstanceBusinessKeySet) {
        List<HistoricProcessInstance> instanceList = this.fetchHistoricLastProcInstanceList(procInstanceBusinessKeySet);
        Map<String, List<HistoricProcessInstance>> instanceMap = Maps.newHashMap();
        for (HistoricProcessInstance historicProcessInstance : instanceList) {
            instanceMap.put(historicProcessInstance.getBusinessKey(), Lists.newArrayList(historicProcessInstance));

            List<HistoricProcessInstance> subInstanceList = this.fetchHistoricProcInstanceBySuperId(historicProcessInstance.getId());
            if (CollectionUtils.isNotEmpty(subInstanceList)) {
                instanceMap.get(historicProcessInstance.getBusinessKey()).addAll(subInstanceList);
            }
        }
        return instanceMap;
    }

    @Override
    public PageUtils<HistoricTaskInstance> fetchHistoricTaskPage(ActHiTaskPageQuery query) {

        HistoricTaskInstanceQuery taskInstanceQuery = historyService.createHistoricTaskInstanceQuery();
//                .processInstanceBusinessKeyLike("%" + query.getBusinessKeyLike() + "%");

        taskInstanceQuery.processDefinitionKeyIn(query.getProcessDefinitionKeyList());

        if (StringUtils.isNotBlank(query.getAssignee())) {
            taskInstanceQuery.taskAssignee(query.getAssignee());
        }
        if (StringUtils.isNotBlank(query.getProcessNode())) {
            taskInstanceQuery.taskDefinitionKey(query.getProcessNode());
        }
        if (CollectionUtils.isNotEmpty(query.getAssigneeList())) {
            taskInstanceQuery.taskAssigneeIds(query.getAssigneeList());
        }
        if (StringUtils.isNotBlank(query.getProcessInstanceBusinessKey())) {
            taskInstanceQuery.processInstanceBusinessKey(query.getProcessInstanceBusinessKey());
        }
        taskInstanceQuery.finished().orderByTaskCreateTime().desc();

        List<HistoricTaskInstance> taskInstanceList = taskInstanceQuery
                .listPage(query.getLimit() * (query.getPage() - 1), query.getLimit());
        Long count = taskInstanceQuery.count();

        PageUtils pageUtils = new PageUtils(taskInstanceList, count.intValue(), query.getLimit(), query.getPage());

        return pageUtils;
    }


    @Override
    public List<HistoricProcessInstance> fetchHistoricProcInstanceList(Set<String> processInstanceIds) {
        if (CollectionUtils.isEmpty(processInstanceIds)) {
            return Lists.newArrayList();
        }
        List<HistoricProcessInstance> instanceList = this.historyService.createHistoricProcessInstanceQuery().processInstanceIds(processInstanceIds).list();
        return instanceList;
    }

    @Override
    public HistoricProcessInstance fetchHistoricProcInstanceById(String processInstanceId) {
        List<HistoricProcessInstance> instanceList = this.fetchHistoricProcInstanceList(Sets.newHashSet(processInstanceId));
        if (CollectionUtils.isEmpty(instanceList)) {
            return null;
        }

        return instanceList.get(0);
    }

    @Override
    public List<HistoricProcessInstance> fetchHistoricProcInstanceBySuperId(String superProcessInstanceId) {
        List<HistoricProcessInstance> instanceList = this.historyService.createHistoricProcessInstanceQuery()
                .superProcessInstanceId(superProcessInstanceId).list();
        return instanceList;
    }

    @Override
    public List<HistoricProcessInstance> fetchUnfinishedHistoricProcInstanceBySuperId(String superProcessInstanceId) {
        List<HistoricProcessInstance> instanceList = this.historyService.createHistoricProcessInstanceQuery()
                .unfinished()
                .superProcessInstanceId(superProcessInstanceId).list();
        return instanceList;
    }

    @Override
    public List<HistoricProcessInstance> fetchFinishedHistoricProcInstanceBySuperId(String superProcessInstanceId) {
        List<HistoricProcessInstance> instanceList = this.historyService.createHistoricProcessInstanceQuery()
                .finished()
                .superProcessInstanceId(superProcessInstanceId).list();
        return instanceList;
    }

    @Override
    public List<HistoricProcessInstance> fetchHistoricProcInstanceAllListById(String processInstanceId) {
        List<HistoricProcessInstance> instanceList = Lists.newArrayList();
        HistoricProcessInstance instance = this.historyService.createHistoricProcessInstanceQuery()
                .processInstanceId(processInstanceId).singleResult();
        if (instance == null) {
            return instanceList;
        }

        instanceList.add(instance);
        List<HistoricProcessInstance> subInstanceList = fetchHistoricProcInstanceBySuperId(instance.getId());
        instanceList.addAll(subInstanceList);
        return instanceList;
    }

    @Override
    public Map<String, String> fetchProcInstanceBusinessKey(Set<String> procInstanceIdSet) {
        Map<String, String> idKeyMap = Maps.newHashMap();
        if (CollectionUtils.isEmpty(procInstanceIdSet)) {
            return idKeyMap;
        }
        List<HistoricProcessInstance> instanceList = this.fetchHistoricProcInstanceList(procInstanceIdSet);
        for (HistoricProcessInstance historicProcessInstance : instanceList) {
            //业务key一定要存在
            String businessKey = historicProcessInstance.getBusinessKey();
            if (StringUtils.isBlank(businessKey)) {
                HistoricProcessInstance rootInstance = this.fetchHistoricProcInstanceById(historicProcessInstance.getSuperProcessInstanceId());
                businessKey = rootInstance.getBusinessKey();
            }
            idKeyMap.put(historicProcessInstance.getId(), businessKey);
        }
        return idKeyMap;
    }
}
