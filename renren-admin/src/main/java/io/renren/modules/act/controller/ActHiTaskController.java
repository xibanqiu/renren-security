package io.renren.modules.act.controller;

import io.renren.common.annotation.SysLog;
import io.renren.common.utils.R;
import io.renren.modules.act.mo.request.ActHiTaskCommentsReq;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @Date 2020/10/29 10:48
 */
@RestController
@RequestMapping("act/acthitask")
public class ActHiTaskController {

    @SysLog("任务历史批注信息")
    @RequestMapping("/commentsList")
    public R fetchTaskCommentsList(ActHiTaskCommentsReq req) {
        return null;
    }
}
