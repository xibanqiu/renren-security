package io.renren.modules.act.service.impl;

import io.renren.common.utils.PageUtils;
import io.renren.common.utils.R;
import io.renren.modules.act.mo.convert.ActInfoConvert;
import io.renren.modules.act.mo.request.*;
import io.renren.modules.act.mo.response.ActRuTaskCommentsRep;
import io.renren.modules.act.mo.response.ActRuTaskOutGoingFlowRep;
import io.renren.modules.act.service.ActRuTaskService;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.bpmn.model.FlowElement;
import org.activiti.bpmn.model.SequenceFlow;
import org.activiti.bpmn.model.UserTask;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.impl.identity.Authentication;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Comment;
import org.activiti.engine.task.Task;
import org.activiti.engine.task.TaskQuery;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 *
 * @Date 2020/7/22 17:11
 */
@Slf4j
@Service("actRuTaskService")
public class ActRuTaskServiceImpl implements ActRuTaskService {
    @Autowired
    private TaskService taskService;
    @Autowired
    private RuntimeService runtimeService;
    @Autowired
    private RepositoryService repositoryService;


    @Override
    public PageUtils<Task> fetchTaskPageQuery(ActRuTaskPageReq pageReq) {

        TaskQuery taskQuery = taskService.createTaskQuery();
//        taskQuery.processInstanceBusinessKeyLike("%" + pageReq.getBusinessKey() + "%");

        taskQuery.processDefinitionKeyIn(pageReq.getProcessDefinitionKeyList());

        if (StringUtils.isNotBlank(pageReq.getAssignee())) {
            taskQuery.taskAssignee(pageReq.getAssignee());
        }
        if (StringUtils.isNotBlank(pageReq.getProcessNode())) {
            taskQuery.taskDefinitionKey(pageReq.getProcessNode());
        }
        if (CollectionUtils.isNotEmpty(pageReq.getAssigneeList())) {
            taskQuery.taskAssigneeIds(pageReq.getAssigneeList());
        }
        if (StringUtils.isNotBlank(pageReq.getCandidateUser())) {
            taskQuery.taskCandidateUser(pageReq.getCandidateUser());
        }
        if (StringUtils.isNotBlank(pageReq.getCandidateGroup())) {
            taskQuery.taskCandidateGroup(pageReq.getCandidateGroup());
        }
        taskQuery.orderByTaskCreateTime().desc();

        List<Task> taskList = taskQuery.listPage(pageReq.getLimit() * (pageReq.getPage() - 1), pageReq.getLimit());
        ;

        Long count = taskQuery.count();

        PageUtils<Task> pageUtils = new PageUtils(taskList, count.intValue(), pageReq.getLimit(), pageReq.getPage());

        return pageUtils;
    }


    @Override
    public R fetchTaskOutGoingFlow(ActRuTaskOutGoingFlowReq flowReq) {

        String procDefineId = flowReq.getProcDefineId();
        String taskDefineKey = flowReq.getTaskDefineKey();
        if (StringUtils.isBlank(procDefineId)
                || StringUtils.isBlank(taskDefineKey)) {
            if (flowReq.getTaskId() == null) {
                log.error("fetchTaskOutGoingFlow taskId is null");
                return R.error("taskId 为空");
            }
            Task task = this.taskService.createTaskQuery().taskId(flowReq.getTaskId()).singleResult();
            procDefineId = task.getProcessDefinitionId();
            taskDefineKey = task.getTaskDefinitionKey();
        }
        if (StringUtils.isBlank(procDefineId) || StringUtils.isBlank(taskDefineKey)) {
            return R.error();
        }
        //获取模型
        BpmnModel bpmnModel = this.repositoryService.getBpmnModel(procDefineId);
        //获取当前节点
        FlowElement flowElement = bpmnModel.getFlowElement(taskDefineKey);
        UserTask userTask = (UserTask) flowElement;
        List<SequenceFlow> flowList = userTask.getOutgoingFlows();

        List<ActRuTaskOutGoingFlowRep> goingFlowRepList = Lists.newArrayList();
        for (SequenceFlow sequenceFlow : flowList) {
            ActRuTaskOutGoingFlowRep goingFlowRep = new ActRuTaskOutGoingFlowRep();
            goingFlowRep.setId(sequenceFlow.getId());
            goingFlowRep.setName(sequenceFlow.getName());

            goingFlowRepList.add(goingFlowRep);
        }
        goingFlowRepList = goingFlowRepList.stream().sorted(Comparator.comparing(ActRuTaskOutGoingFlowRep::getName)).collect(Collectors.toList());
        return R.ok().putList(goingFlowRepList);
    }


    @Override
    public R fetchTaskCommentsList(ActRuTaskCommentsReq req) {
        String procInstanceId = req.getProcInstanceId();
        if (StringUtils.isBlank(procInstanceId)) {
            if (StringUtils.isBlank(req.getTaskId())) {
                return R.error();
            }
            Task task = this.taskService.createTaskQuery().taskId(req.getTaskId()).singleResult();
            procInstanceId = task.getProcessInstanceId();
        }
        if (StringUtils.isBlank(procInstanceId)) {
            return R.error();
        }
        List<Comment> commentList = this.taskService.getProcessInstanceComments(procInstanceId);

        List<ActRuTaskCommentsRep> repList = ActInfoConvert.convertTaskCommentsDBToRep(commentList);
        return R.ok().putList(repList);
    }

    @Override
    public R claimTask(ActRuTaskClaimReq req, String sysUserName) {
        if (StringUtils.isBlank(req.getTaskId())) {
            return R.error();
        }
        try {
            taskService.claim(req.getTaskId(), sysUserName);
            return R.ok();
        } catch (Throwable e) {
            log.error("claimTask", e);
        }

        return R.error("已认领 请刷新");
    }

    @Override
    public R unClaimTask(ActRuTaskClaimReq req, String sysUserName) {
        if (StringUtils.isBlank(req.getTaskId())) {
            return R.error();
        }
        try {
            taskService.unclaim(req.getTaskId());
            return R.ok();
        } catch (Throwable e) {
            log.error("unClaimTask", e);
        }

        return R.error();
    }

    @Transactional
    @Override
    public R completeTask(ActRuTaskCompleteReq req, String sysUserName) {
        if (StringUtils.isBlank(req.getTaskId())) {
            return R.error();
        }
        String taskId = req.getTaskId();
        String processInstanceId = req.getProcInstanceId();
        String procDefId;
        if (StringUtils.isBlank(processInstanceId)) {
            Task task = this.taskService.createTaskQuery().taskId(taskId).singleResult();
            processInstanceId = task.getProcessInstanceId();
            procDefId = task.getProcessDefinitionId();
        }
        if (StringUtils.isBlank(processInstanceId)) {
            return R.error();
        }
        //设置批注人名
        Authentication.setAuthenticatedUserId(sysUserName);
        //添加批注
        this.taskService.addComment(taskId, processInstanceId, "[" + req.getOutcome() + "] " + req.getComment());
        //取消
        this.taskService.unclaim(taskId);
        //认领
        this.taskService.claim(taskId, sysUserName);
        //完成任务
        Map<String, Object> variables = Maps.newHashMap();
        if (StringUtils.isNotBlank(req.getOutcome())) {
            variables.put("outcome", req.getOutcome());
        }
        this.taskService.complete(taskId, variables);
        setPatentStatus(processInstanceId);
        return R.ok();
    }

    /**
     * 根据流程实例id 设置 流程的状态
     *
     * @param processInstanceId ：流程实例id
     */
    @Override
    public void setPatentStatus(String processInstanceId) {
        if (processInstanceId == null) {
            return;
        }
        //3.使用流程实例，查询
        ProcessInstance pi = runtimeService.createProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();
        if (pi == null) {
            return;
        }
        //1.使用流程实例对象processInstanceId 获取BusinessKey
        String businessKey = pi.getBusinessKey();
        //2.获取Business_key对应的主键ＩＤ
        if (StringUtils.isNotBlank(businessKey)) {
            Task task = this.taskService.createTaskQuery().processInstanceId(processInstanceId).singleResult();
            Integer statusId;
            //当进入到第一层if中,表明主流程已经执行；如果通过流程id查询出来的结果为空,表明流程已经执行完成
            //100:结束状态
            if (task == null) {
                statusId = 100;
            } else {
                statusId = getStatusId(task.getName());
            }
            String patentId = businessKey.split(":")[1];

        }
    }

    private static Integer getStatusId(String name) {
        HashMap<String, Integer> statusMaps = Maps.newHashMap();
        statusMaps.put("未提交", 1);
        statusMaps.put("内审退改", 2);
        statusMaps.put("技术审核", 3);
        statusMaps.put("技术退改", 4);
        statusMaps.put("OA审核", 5);
        statusMaps.put("分配代理", 6);
        statusMaps.put("代理处理", 7);
        statusMaps.put("资质审批", 8);
        statusMaps.put("知识产权局受理", 9);
        statusMaps.put("知识产权局初审", 10);
        statusMaps.put("实质审核通知", 11);
        statusMaps.put("实质审核意见", 12);
        statusMaps.put("实质复审资质审核", 13);
        statusMaps.put("实质复审决策", 14);
        statusMaps.put("初审费用", 15);
        statusMaps.put("外观审核意见", 16);
        statusMaps.put("专利授权确认", 17);
        statusMaps.put("实质复审决议", 18);
        statusMaps.put("实质复审材料", 19);
        statusMaps.put("证书费用", 20);
        statusMaps.put("复审费用", 21);
        statusMaps.put("上传证书", 22);
        statusMaps.put("第二次激励", 23);
        statusMaps.put("补贴流程", 24);
        statusMaps.put("内部审核", 25);

        statusMaps.put("专利主流程完成", 100);
        return statusMaps.get(name);
    }


    @Override
    public Comment addActiveTaskComment(String executionId, String message) {
        //获取当前活动任务
        Task task = taskService.createTaskQuery().executionId(executionId).active().singleResult();

        return taskService.addComment(task.getId(), task.getProcessInstanceId(), message);
    }

    @Override
    public boolean subTypeQueryByProDefIdAndName(String procDefId, String subTypeName) {
        List<Task> taskList = taskService.createTaskQuery().processDefinitionId(procDefId).list();
        if (taskList != null) {
            for (Task li : taskList) {
                if (li.getName().contains(subTypeName)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean subTypeQueryByProInstIdAndName(String procInstId, String subTypeName) {
        List<Task> taskList = taskService.createTaskQuery().processInstanceId(procInstId).list();
        if (taskList != null) {
            for (Task li : taskList) {
                if (li.getName().contains(subTypeName)) {
                    return true;
                }
            }
        }
        return false;
    }

}
