package io.renren.modules.act.controller;


import io.renren.common.annotation.SysLog;
import io.renren.common.utils.R;
import io.renren.modules.act.mo.request.*;
import io.renren.modules.act.service.ActRuTaskService;
import io.renren.modules.sys.controller.AbstractController;
import io.renren.modules.sys.entity.SysUserEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 *
 * @Date 2020/7/22 17:07
 */
@RestController
@RequestMapping("act/actrutask")
public class ActRuTaskController extends AbstractController {

    @Autowired
    private ActRuTaskService actRuTaskService;

    @SysLog("任务列表 分页")
    @RequestMapping("/page")
    public R taskPage(@RequestBody ActRuTaskPageReq req) {

        return R.ok();
    }

    @SysLog("任务的出口信息")
    @RequestMapping("/outGoingFlowList")
    public R fetchTaskOutGoingFlow(ActRuTaskOutGoingFlowReq flowReq) {

        return this.actRuTaskService.fetchTaskOutGoingFlow(flowReq);
    }

    @SysLog("任务批注信息")
    @RequestMapping("/commentsList")
    public R fetchTaskCommentsList(ActRuTaskCommentsReq req) {

        return this.actRuTaskService.fetchTaskCommentsList(req);
    }

    @SysLog("认领任务")
    @RequestMapping("/claimTask")
    public R claimTask(@RequestBody ActRuTaskClaimReq req) {
        SysUserEntity sysUserEntity = this.getUser();
        if (sysUserEntity == null) {
            return R.error("未登录");
        }
        return this.actRuTaskService.claimTask(req, sysUserEntity.getUsername());
    }

    @SysLog("退回任务")
    @RequestMapping("/unClaimTask")
    public R unClaimTask(@RequestBody ActRuTaskClaimReq req) {
        SysUserEntity sysUserEntity = this.getUser();
        if (sysUserEntity == null) {
            return R.error("未登录");
        }
        return this.actRuTaskService.unClaimTask(req, sysUserEntity.getUsername());
    }

    @SysLog("完成任务")
    @RequestMapping("/completeTask")
    public R completeTask(@RequestBody ActRuTaskCompleteReq req) {
        SysUserEntity sysUserEntity = this.getUser();
        if (sysUserEntity == null) {
            return R.error("未登录");
        }
        return this.actRuTaskService.completeTask(req, sysUserEntity.getUsername());
    }
}
