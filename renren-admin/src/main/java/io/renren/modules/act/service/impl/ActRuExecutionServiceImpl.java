package io.renren.modules.act.service.impl;

import io.renren.common.exception.RRException;
import io.renren.common.utils.PageUtils;
import io.renren.modules.act.mo.domain.ManualFlowElement;
import io.renren.modules.act.mo.query.ActRuExecutionPageQuery;
import io.renren.modules.act.mo.query.ActRuProcessPageQuery;
import io.renren.modules.act.service.ActRuExecutionService;
import io.renren.modules.act.service.impl.image.CustomProcessDiagramGenerator;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.activiti.bpmn.model.*;
import org.activiti.engine.HistoryService;
import org.activiti.engine.ProcessEngineConfiguration;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.runtime.Execution;
import org.activiti.engine.runtime.ExecutionQuery;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.runtime.ProcessInstanceQuery;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.*;

/**
 *
 * @Date 2020/7/22 16:25
 */
@Slf4j
@Service("actRuExecutionService")
public class ActRuExecutionServiceImpl implements ActRuExecutionService {
    @Autowired
    private RuntimeService runtimeService;
    @Autowired
    private HistoryService historyService;
    @Autowired
    private RepositoryService repositoryService;
    @Autowired
    private ProcessEngineConfiguration processEngineConfiguration;

    @Override
    public PageUtils<ProcessInstance> fetchRuProcessPage(ActRuProcessPageQuery query) {
        ProcessInstanceQuery instanceQuery = this.runtimeService.createProcessInstanceQuery();
        List<ProcessInstance> processInstanceList = instanceQuery
                .orderByProcessInstanceId().desc()
                .listPage(query.getLimit() * (query.getPage() - 1), query.getLimit());

        Long count = instanceQuery.count();

        PageUtils pageUtils = new PageUtils(processInstanceList, count.intValue(),
                query.getLimit(), query.getPage());
        return pageUtils;
    }

    @Override
    public PageUtils<Execution> fetchRuExecutionPage(ActRuExecutionPageQuery query) {
        ExecutionQuery executionQuery = this.runtimeService.createExecutionQuery();

        if (CollectionUtils.isNotEmpty(query.getProcDefinitionKeySet())) {
            executionQuery.processDefinitionKeys(query.getProcDefinitionKeySet());
        }
        executionQuery.onlyChildExecutions();
        executionQuery.orderByProcessInstanceId().desc();
        List<Execution> executionList = executionQuery.listPage(query.getLimit() * (query.getPage() - 1), query.getLimit());
        Long count = executionQuery.count();

        PageUtils pageUtils = new PageUtils(executionList, count.intValue(),
                query.getLimit(), query.getPage());
        return pageUtils;
    }

    @Override
    public String startProcess(String procDefKey, String businessKey, Map<String, Object> variables) {

        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(procDefKey, businessKey, variables);
        return processInstance.getId();
    }

    @Override
    public InputStream fetchProcessImg(String procInstanceId) {

        if (StringUtils.isBlank(procInstanceId)) {
            throw new RRException("流程实例Id为空");
        }

        List<Execution> runningActivityInstanceList = runtimeService.createExecutionQuery().processInstanceId(procInstanceId).list();
        List<String> runningActivitiIdList = Lists.newArrayList();
        for (Execution execution : runningActivityInstanceList) {
            if (org.apache.commons.lang3.StringUtils.isNotEmpty(execution.getActivityId())) {
                runningActivitiIdList.add(execution.getActivityId());
                log.info("执行中的节点[{}-{}-{}]", execution.getId(), execution.getActivityId(), execution.getName());
            }
        }

        //获取执行的流程实例
        HistoricProcessInstance historicProcessInstance = this.historyService.createHistoricProcessInstanceQuery().processInstanceId(procInstanceId).singleResult();
        //获取已经执行的节点活动
        List<HistoricActivityInstance> historicActivityInstanceList = this.historyService.createHistoricActivityInstanceQuery().processInstanceId(procInstanceId)
                .orderByHistoricActivityInstanceId().asc()
                .list();

        //需要高亮执行过的活动节点
        List<String> highlightActivitiIds = Lists.newArrayList();
        for (HistoricActivityInstance historicActivityInstance : historicActivityInstanceList) {
            highlightActivitiIds.add(historicActivityInstance.getActivityId());
        }


//        List<HistoricProcessInstance> historicProcessInstanceList = this.historyService.createHistoricProcessInstanceQuery().processInstanceId(procInstanceId).finished().list();

        //ProcessDiagramGenerator processDiagramGenerator = processEngineConfiguration.getProcessDiagramGenerator();
        CustomProcessDiagramGenerator processDiagramGenerator;
         processDiagramGenerator = new CustomProcessDiagramGenerator();
        BpmnModel bpmnModel = this.repositoryService.getBpmnModel(historicProcessInstance.getProcessDefinitionId());

        List<String> highLightedFlowIds = getHighLightedFlows(bpmnModel, historicActivityInstanceList);
        InputStream inputStream = processDiagramGenerator
                .generateDiagramCustom(bpmnModel, "jpg",
                highlightActivitiIds, runningActivitiIdList,highLightedFlowIds,
                "微软雅黑",
                "微软雅黑",
                "微软雅黑",
                null,
                1.0D
                );

//        InputStream inputStream = processDiagramGenerator.generateDiagram(
//                bpmnModel, "png",
//                highlightActivitiIds
//        );

        return inputStream;
    }

    /**
     * 获取已经流转的线
     *
     * @param bpmnModel
     * @param historicActivityInstances
     * @return
     */
    private static List<String> getHighLightedFlow(BpmnModel bpmnModel, List<HistoricActivityInstance> historicActivityInstances) {
        // 高亮流程已发生流转的线id集合
        List<String> highLightedFlowIds = Lists.newArrayList();
        // 全部活动节点
        List<FlowNode> historicActivityNodes = Lists.newArrayList();
        // 已完成的历史活动节点
        List<HistoricActivityInstance> finishedActivityInstances = Lists.newArrayList();

        for (HistoricActivityInstance historicActivityInstance : historicActivityInstances) {
            FlowNode flowNode = (FlowNode) bpmnModel.getMainProcess().getFlowElement(historicActivityInstance.getActivityId(), true);
            historicActivityNodes.add(flowNode);
            if (historicActivityInstance.getEndTime() != null) {
                finishedActivityInstances.add(historicActivityInstance);
            }
        }

        FlowNode currentFlowNode = null;
        FlowNode targetFlowNode = null;
        // 遍历已完成的活动实例，从每个实例的outgoingFlows中找到已执行的
        for (HistoricActivityInstance currentActivityInstance : finishedActivityInstances) {
            // 获得当前活动对应的节点信息及outgoingFlows信息
            currentFlowNode = (FlowNode) bpmnModel.getMainProcess().getFlowElement(currentActivityInstance.getActivityId(), true);
            List<SequenceFlow> sequenceFlows = currentFlowNode.getOutgoingFlows();


            if ("parallelGateway".equals(currentActivityInstance.getActivityType()) || "inclusiveGateway".equals(currentActivityInstance.getActivityType())) {
                // 遍历历史活动节点，找到匹配流程目标节点的
                for (SequenceFlow sequenceFlow : sequenceFlows) {
                    targetFlowNode = (FlowNode) bpmnModel.getMainProcess().getFlowElement(sequenceFlow.getTargetRef(),true);
                    if (sequenceFlows.contains(targetFlowNode)) {
                        highLightedFlowIds.add(sequenceFlow.getId());
                    }
                }
            } else {
                List<Map<String, String>> tempMapList = new LinkedList<Map<String, String>>();
                //这里添加判断为多分支的情况下，取出该任务的id
                int sourceId=0;
                if (sequenceFlows.size() > 1) {
                    for (SequenceFlow sequenceFlow : sequenceFlows) {
                        for (HistoricActivityInstance historicActivityInstance : historicActivityInstances) {
                            if (sequenceFlow.getSourceRef().equals(historicActivityInstance.getActivityId())) {
                                sourceId = Integer.parseInt(historicActivityInstance.getId());
                                break;
                            }
                        }
                        break;
                    }
                }
                // 遍历历史活动节点，找到匹配Flow目标节点的  ,根据先执行的任务小于后执行任务id，添加该节点执行的id是否小于流程分支的执行id判断是否需要高亮, 这种方法不适用并发下的uuid主键生成策略
                for (SequenceFlow sequenceFlow : sequenceFlows) {
                    for (HistoricActivityInstance historicActivityInstance : historicActivityInstances) {
                        if (historicActivityInstance.getActivityId().equals(sequenceFlow.getTargetRef())&&sourceId<Integer.parseInt(historicActivityInstance.getId())) {
                            Map<String, String> map = Maps.newHashMap();
                            map.put("highLightedFlowId", sequenceFlow.getId());
                            map.put("highLightedFlowStartTime", String.valueOf(historicActivityInstance.getStartTime().getTime()));
                            tempMapList.add(map);
                        }
                    }
                }
                //这里去掉多次执行取时间开始最早的代码
                long earliestStamp = 0L;
                String flowId = null;
                for (Map<String, String> map : tempMapList) {
                    flowId = map.get("highLightedFlowId");
                    highLightedFlowIds.add(flowId);
                }
            }
        }
        return highLightedFlowIds;
    }

    /**
     * 获取已流经的流程线，需要高亮显示高亮流程已发生流转的线id集合
     *
     * @param bpmnModel
     * @param historicActivityInstanceList
     * @return
     */
    public static List<String> getHighLightedFlows(BpmnModel bpmnModel, List<HistoricActivityInstance> historicActivityInstanceList) {
        // 已流经的流程线，需要高亮显示
        List<String> highLightedFlowIdList = Lists.newArrayList();
        // 全部活动节点
        List<FlowNode> allHistoricActivityNodeList = Lists.newArrayList();
        // 已完成的历史活动节点
        List<HistoricActivityInstance> finishedActivityInstanceList = Lists.newArrayList();

        for (HistoricActivityInstance historicActivityInstance : historicActivityInstanceList) {
            // 获取流程节点
            FlowNode flowNode = (FlowNode) bpmnModel.getMainProcess().getFlowElement(historicActivityInstance.getActivityId(),
                    true);
            allHistoricActivityNodeList.add(flowNode);
            // 结束时间不为空，当前节点则已经完成
            if (historicActivityInstance.getEndTime() != null) {
                finishedActivityInstanceList.add(historicActivityInstance);
            }
        }

        FlowNode currentFlowNode = null;
        FlowNode targetFlowNode = null;
        HistoricActivityInstance currentActivityInstance;
        // 遍历已完成的活动实例，从每个实例的outgoingFlows中找到已执行的
        for (int k = 0; k < finishedActivityInstanceList.size(); k++) {
            currentActivityInstance = finishedActivityInstanceList.get(k);
            // 获得当前活动对应的节点信息及outgoingFlows信息
            currentFlowNode = (FlowNode) bpmnModel.getMainProcess().getFlowElement(currentActivityInstance.getActivityId(),
                    true);
            // 当前节点的所有流出线
            List<SequenceFlow> outgoingFlowList = currentFlowNode.getOutgoingFlows();

            /**
             * 遍历outgoingFlows并找到已流转的 满足如下条件认为已流转：
             * 1.当前节点是并行网关或兼容网关，则通过outgoingFlows能够在历史活动中找到的全部节点均为已流转
             * 2.当前节点是以上两种类型之外的，通过outgoingFlows查找到的时间最早的流转节点视为有效流转
             * (第2点有问题，有过驳回的，会只绘制驳回的流程线，通过走向下一级的流程线没有高亮显示)
             */
            if ("parallelGateway".equals(currentActivityInstance.getActivityType()) || "inclusiveGateway".equals(
                    currentActivityInstance.getActivityType())) {
                // 遍历历史活动节点，找到匹配流程目标节点的
                for (SequenceFlow outgoingFlow : outgoingFlowList) {
                    // 获取当前节点流程线对应的下级节点
                    targetFlowNode = (FlowNode) bpmnModel.getMainProcess().getFlowElement(outgoingFlow.getTargetRef(),
                            true);
                    // 如果下级节点包含在所有历史节点中，则将当前节点的流出线高亮显示
                    if (allHistoricActivityNodeList.contains(targetFlowNode)) {
                        highLightedFlowIdList.add(outgoingFlow.getId());
                    }
                }
            } else {
                /**
                 * 2、当前节点不是并行网关或兼容网关
                 * 【已解决-问题】如果当前节点有驳回功能，驳回到申请节点，
                 * 则因为申请节点在历史节点中，导致当前节点驳回到申请节点的流程线被高亮显示，但实际并没有进行驳回操作
                 */
                List<Map<String, Object>> tempMapList = new ArrayList<>();
                // 当前节点ID
                String currentActivityId = currentActivityInstance.getActivityId();
                int size = historicActivityInstanceList.size();
                boolean ifStartFind = false;
                boolean ifFinded = false;
                HistoricActivityInstance historicActivityInstance;
                // 循环当前节点的所有流出线
                // 循环所有历史节点
                log.info("【开始】-匹配当前节点-ActivityId=【{}】需要高亮显示的流出线", currentActivityId);
                log.info("循环历史节点");
                for (int i = 0; i < historicActivityInstanceList.size(); i++) {
                    // // 如果当前节点流程线对应的下级节点在历史节点中，则该条流程线进行高亮显示（【问题】有驳回流程线时，即使没有进行驳回操作，因为申请节点在历史节点中，也会将驳回流程线高亮显示-_-||）
                    // if (historicActivityInstance.getActivityId().equals(sequenceFlow.getTargetRef())) {
                    // Map<String, Object> map = new HashMap<>();
                    // map.put("highLightedFlowId", sequenceFlow.getId());
                    // map.put("highLightedFlowStartTime", historicActivityInstance.getStartTime().getTime());
                    // tempMapList.add(map);
                    // // highLightedFlowIdList.add(sequenceFlow.getId());
                    // }
                    // 历史节点
                    historicActivityInstance = historicActivityInstanceList.get(i);
                    log.info("第【{}/{}】个历史节点-ActivityId=[{}]", i + 1, size, historicActivityInstance.getActivityId());
                    // 如果循环历史节点中的id等于当前节点id，从当前历史节点继续先后查找是否有当前节点流程线等于的节点
                    // 历史节点的序号需要大于等于已完成历史节点的序号，防止驳回重审一个节点经过两次是只取第一次的流出线高亮显示，第二次的不显示
                    if (i >= k && historicActivityInstance.getActivityId().equals(currentActivityId)) {
                        log.info("第[{}]个历史节点和当前节点一致-ActivityId=[{}]", i + 1, historicActivityInstance
                                .getActivityId());
                        ifStartFind = true;
                        // 跳过当前节点继续查找下一个节点
                        continue;
                    }
                    if (ifStartFind) {
                        log.info("[开始]-循环当前节点-ActivityId=【{}】的所有流出线", currentActivityId);

                        ifFinded = false;
                        for (SequenceFlow sequenceFlow : outgoingFlowList) {
                            // 如果当前节点流程线对应的下级节点在其后面的历史节点中，则该条流程线进行高亮显示
                            // 【问题】
                            log.info("当前流出线的下级节点=[{}]", sequenceFlow.getTargetRef());
                            if (historicActivityInstance.getActivityId().equals(sequenceFlow.getTargetRef())) {
                                log.info("当前节点[{}]需高亮显示的流出线=[{}]", currentActivityId, sequenceFlow.getId());
                                highLightedFlowIdList.add(sequenceFlow.getId());
                                // 暂时默认找到离当前节点最近的下一级节点即退出循环，否则有多条流出线时将全部被高亮显示
                                ifFinded = true;
                                break;
                            }
                        }
                        log.info("[完成]-循环当前节点-ActivityId=【{}】的所有流出线", currentActivityId);
                    }
                    if (ifFinded) {
                        // 暂时默认找到离当前节点最近的下一级节点即退出历史节点循环，否则有多条流出线时将全部被高亮显示
                        break;
                    }
                }
                log.info("【完成】-匹配当前节点-ActivityId=【{}】需要高亮显示的流出线", currentActivityId);
                // if (!CollectionUtils.isEmpty(tempMapList)) {
                // // 遍历匹配的集合，取得开始时间最早的一个
                // long earliestStamp = 0L;
                // String highLightedFlowId = null;
                // for (Map<String, Object> map : tempMapList) {
                // long highLightedFlowStartTime = Long.valueOf(map.get("highLightedFlowStartTime").toString());
                // if (earliestStamp == 0 || earliestStamp <= highLightedFlowStartTime) {
                // highLightedFlowId = map.get("highLightedFlowId").toString();
                // earliestStamp = highLightedFlowStartTime;
                // }
                // }
                // highLightedFlowIdList.add(highLightedFlowId);
                // }
            }
        }
        return highLightedFlowIdList;
    }


    @Override
    public List<ProcessInstance> fetchProcessInstance(Set<String> procInstanceIdSet) {
        List<ProcessInstance> instanceList = this.runtimeService.createProcessInstanceQuery().processInstanceIds(procInstanceIdSet).list();

        return instanceList;
    }

    @Override
    public ProcessInstance fetchProcessInstance(String procInstanceId) {
        return this.runtimeService.createProcessInstanceQuery().processInstanceId(procInstanceId).singleResult();
    }

    @Override
    public Map<String, String> fetchProcInstanceBusinessKey(Set<String> procInstanceIdSet) {
        Map<String, String> idKeyMap = Maps.newHashMap();
        if (CollectionUtils.isEmpty(procInstanceIdSet)) {
            return idKeyMap;
        }
        List<ProcessInstance> instanceList = this.fetchProcessInstance(procInstanceIdSet);
        for (ProcessInstance processInstance : instanceList) {
            //业务key一定要存在
            String businessKey = processInstance.getBusinessKey();
            if (StringUtils.isBlank(businessKey)) {
                ProcessInstance rootInstance = this.fetchProcessInstance(processInstance.getRootProcessInstanceId());
                businessKey = rootInstance.getBusinessKey();
            }
            idKeyMap.put(processInstance.getId(), businessKey);
        }
        return idKeyMap;
    }

    @Override
    public Map<String, List<ManualFlowElement>> fetchProcActivityNameMap(Set<String> procBusinessKeyList) {
        Map<String, List<ManualFlowElement>> activityNameMap = Maps.newHashMap();

        for (String businessKey : procBusinessKeyList) {
            activityNameMap.put(businessKey, Lists.newArrayList());

            ProcessInstance processInstance = this.runtimeService.createProcessInstanceQuery().processInstanceBusinessKey(businessKey).singleResult();
            if (processInstance == null) {
                //没有运行中的流程实例
                continue;
            }
            String executionId = processInstance.getId();
            List<String> activityIds = this.runtimeService.getActiveActivityIds(executionId);

            BpmnModel bpmnModel = this.repositoryService.getBpmnModel(processInstance.getProcessDefinitionId());
            for (String activityId : activityIds) {
                FlowElement flowElement = bpmnModel.getFlowElement(activityId);

                ManualFlowElement manualFlowElement = new ManualFlowElement();
                manualFlowElement.setName(flowElement.getName());
                manualFlowElement.setDocumentation(flowElement.getDocumentation());

                String assignee = null;
                if (flowElement instanceof UserTask) {
                    assignee = ((UserTask) flowElement).getAssignee();
                    assignee = assignee.replace("${", "");
                    assignee = assignee.replace("}", "");
                    Object curVar = this.runtimeService.getVariable(executionId, assignee);
                    manualFlowElement.setAssignee(curVar == null ? assignee : curVar.toString());
                }
                activityNameMap.get(businessKey).add(manualFlowElement);
            }
        }

        return activityNameMap;
    }
}
