package io.renren.modules.act.service;

import io.renren.common.utils.PageUtils;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.bpmn.model.FlowElement;
import org.activiti.engine.repository.ProcessDefinition;

import java.util.List;
import java.util.Map;

/**
 * 
 *
 * @author ywz
 * @email weizhao.yao@yintech.cn
 * @date 2020-07-21 16:13:17
 */
public interface ActReProcdefService {

    PageUtils queryPage(Map<String, Object> params);


    BpmnModel fetchBpmnModel(String procDefineId);

    List<FlowElement> fetchMainProcBpmnFlowElements(String procDefineId);

    Map<String, FlowElement> fetchBpmnFlowElementMap(String procDefineId);

    FlowElement fetchBpmnFlowElement(String procDefineId, String activityId);

    List<ProcessDefinition> fetchProcessDefinitionAllList(String processDefinitionKey, boolean latestVersion);
}

