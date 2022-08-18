package io.renren.modules.act.service.impl;

import io.renren.common.utils.PageUtils;
import io.renren.modules.act.service.ActReProcdefService;
import com.google.common.collect.Lists;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.bpmn.model.FlowElement;
import org.activiti.bpmn.model.Process;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.repository.ProcessDefinitionQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;


@Service("actReProcdefService")
public class ActReProcdefServiceImpl implements ActReProcdefService {

    @Autowired
    private RepositoryService repositoryService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {

        return new PageUtils(null);
    }

    @Override
    public BpmnModel fetchBpmnModel(String procDefineId) {
        BpmnModel bpmnModel = this.repositoryService.getBpmnModel(procDefineId);

        return bpmnModel;
    }

    @Override
    public List<FlowElement> fetchMainProcBpmnFlowElements(String procDefineId) {
        BpmnModel bpmnModel = this.fetchBpmnModel(procDefineId);

        return Lists.newArrayList(bpmnModel.getMainProcess().getFlowElements());
    }

    @Override
    public Map<String, FlowElement> fetchBpmnFlowElementMap(String procDefineId) {

        BpmnModel bpmnModel = this.fetchBpmnModel(procDefineId);
        Process process = bpmnModel.getMainProcess();
        return process.getFlowElementMap();
    }

    @Override
    public FlowElement fetchBpmnFlowElement(String procDefineId, String activityId) {
        Map<String, FlowElement> flowElementMap = this.fetchBpmnFlowElementMap(procDefineId);
        return flowElementMap.get(activityId);
    }

    @Override
    public List<ProcessDefinition>  fetchProcessDefinitionAllList(String processDefinitionKey, boolean latestVersion) {
        ProcessDefinitionQuery definitionQuery = this.repositoryService.createProcessDefinitionQuery();
        definitionQuery.processDefinitionKey(processDefinitionKey);
        if (latestVersion) {
            definitionQuery.latestVersion();
        }
        definitionQuery.orderByProcessDefinitionVersion().desc();
        List<ProcessDefinition> definitionList = definitionQuery.list();

        return definitionList;
    }
}