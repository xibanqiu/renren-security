package io.renren.modules.act.controller;

import io.renren.common.annotation.SysLog;
import io.renren.common.utils.R;
import io.renren.modules.act.mo.query.ActHiActivityListQuery;
import io.renren.modules.act.mo.request.ActHiActivityListReq;
import io.renren.modules.act.mo.response.ActHiActivityInfoRep;
import io.renren.modules.act.service.ActHiActivityService;
import com.google.common.collect.Lists;
import org.activiti.engine.history.HistoricActivityInstance;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 *
 * @Date 2020/11/2 13:14
 */
@RestController
@RequestMapping("act/acthiactivity")
public class ActHiActivityController {
    @Autowired
    private ActHiActivityService actHiActivityService;

    @SysLog(value = "历史活动 待处理")
    @RequestMapping("activity/dealList")
    public R fetchActHisActivityList(ActHiActivityListReq req) {
        ActHiActivityListQuery query = new ActHiActivityListQuery();
        BeanUtils.copyProperties(req, query);
        query.setFinished(false);

        List<HistoricActivityInstance> instanceList = this.actHiActivityService.fetchActHisActivityList(query);

        List<ActHiActivityInfoRep> infoRepList = Lists.newArrayList();
        for (HistoricActivityInstance historicActivityInstance : instanceList) {
//            ActivitiEventType
            if ("callActivity".equals(historicActivityInstance.getActivityType())) {
                continue;
            }
            ActHiActivityInfoRep infoRep = new ActHiActivityInfoRep();
            infoRep.setName(historicActivityInstance.getActivityName());
            infoRep.setAssignee(StringUtils.isBlank(historicActivityInstance.getAssignee()) ? "无" : historicActivityInstance.getAssignee());
            infoRepList.add(infoRep);
        }

        return R.ok().putList(infoRepList);
    }
}
