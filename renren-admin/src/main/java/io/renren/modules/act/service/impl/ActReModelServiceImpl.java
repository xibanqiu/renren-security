package io.renren.modules.act.service.impl;

import io.renren.common.utils.PageUtils;
import io.renren.modules.act.service.ActReModelService;
import com.google.common.collect.Maps;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.bpmn.model.FlowElement;
import org.activiti.bpmn.model.UserTask;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.runtime.ProcessInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;


@Service("actReModelService")
public class ActReModelServiceImpl implements ActReModelService {
    @Autowired
    private RepositoryService repositoryService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {


        return null;
    }

    @Override
    public Map<String, String> getActivityNameByProcessInstance(List<ProcessInstance> processInstanceList) {
        Map<String, String> activityNameMap = Maps.newHashMap();
        for (ProcessInstance processInstance : processInstanceList) {
            //获取模型

            BpmnModel bpmnModel = this.repositoryService.getBpmnModel(processInstance.getProcessDefinitionId());
            processInstance.getActivityId();
            //获取当前节点
            FlowElement flowElement = bpmnModel.getFlowElement(processInstance.getActivityId());
            UserTask userTask = (UserTask) flowElement;
            String name = userTask.getName();

            activityNameMap.put(processInstance.getActivityId(), name);
        }

        return activityNameMap;
    }

}