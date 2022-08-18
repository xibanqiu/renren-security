package io.renren.modules.act.service;

import io.renren.common.utils.R;
import io.renren.modules.act.mo.request.ActHiTaskCommentsReq;

/**
 *
 * @Date 2020/10/29 10:51
 */
public interface ActHiTaskService {
    R fetchTaskCommentsList(ActHiTaskCommentsReq req);
}
