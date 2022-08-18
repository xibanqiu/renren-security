package io.renren.modules.act.service;


import io.renren.common.utils.PageUtils;
import io.renren.common.utils.R;
import io.renren.modules.act.mo.request.*;
import org.activiti.engine.task.Comment;
import org.activiti.engine.task.Task;

/**
 *
 * @Date 2020/7/22 17:11
 */
public interface ActRuTaskService {
    PageUtils<Task> fetchTaskPageQuery(ActRuTaskPageReq pageReq);

    R fetchTaskOutGoingFlow(ActRuTaskOutGoingFlowReq flowReq);

    R fetchTaskCommentsList(ActRuTaskCommentsReq req);

    R claimTask(ActRuTaskClaimReq req, String sysUserName);

    R unClaimTask(ActRuTaskClaimReq req, String sysUserName);

    R completeTask(ActRuTaskCompleteReq req, String sysUserName);

    Comment addActiveTaskComment(String executionId, String message);

    boolean subTypeQueryByProDefIdAndName(String procInstanceId, String subTypeName);

    boolean subTypeQueryByProInstIdAndName(String procInstanceId, String subTypeName);


    public void setPatentStatus(String processInstanceId);

}
