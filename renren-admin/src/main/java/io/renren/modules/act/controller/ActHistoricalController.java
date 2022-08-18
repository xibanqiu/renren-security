package io.renren.modules.act.controller;

import io.renren.common.annotation.SysLog;
import io.renren.common.utils.R;
import io.renren.modules.act.mo.query.ActHiActivityListQuery;
import io.renren.modules.act.mo.request.ActHisProcessAllReq;
import io.renren.modules.act.mo.response.ActHisProcessInfoRep;
import io.renren.modules.act.service.ActHiActivityService;
import io.renren.modules.act.service.ActHistoricService;
import com.google.common.collect.Lists;
import org.activiti.engine.history.HistoricProcessInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 *
 * @Date 2020/7/23 18:25
 */
@RestController
@RequestMapping("act/acthistorical")
public class ActHistoricalController {

    @Autowired
    private ActHistoricService actHistoricService;
    @Autowired
    private ActHiActivityService actHiActivityService;

    @SysLog(value = "历史流程信息 id")
    @RequestMapping("/processInfo/byId")
    public R fetchProcessAllById(ActHisProcessAllReq req) {

        List<ActHisProcessInfoRep> infoRepList = Lists.newArrayList();
        List<HistoricProcessInstance> instanceList = this.actHistoricService.fetchHistoricProcInstanceAllListById(req.getProcInstanceId());
        for (HistoricProcessInstance historicProcessInstance : instanceList) {
            ActHiActivityListQuery query = new ActHiActivityListQuery();
            query.setProcInstanceId(historicProcessInstance.getId());
            query.setFinished(false);
//            List<HistoricActivityInstance> activityInstanceList = actHiActivityService.fetchActHisActivityList(query);
//            List<ActHisProcessInfoRep.ActHisActivityInfoRep> activityInfoRepList = ActInfoConvert.covertHiActivityToRep(activityInstanceList);
            ActHisProcessInfoRep infoRep = new ActHisProcessInfoRep();
            infoRep.setProcInstanceId(historicProcessInstance.getId());
            infoRep.setProcDefinitionName(historicProcessInstance.getProcessDefinitionName());

//            infoRep.setProcDefinitionNameCn(IprProcessDefKeyEnum.processEnumMap.get(historicProcessInstance.getProcessDefinitionKey()));
            infoRep.setProcDefinitionNameCn("test");
            infoRep.setStartTime(historicProcessInstance.getStartTime());
            infoRep.setEndTime(historicProcessInstance.getEndTime());
            infoRep.setDurationInMillis(historicProcessInstance.getDurationInMillis());
            infoRep.setDeleteReason(historicProcessInstance.getDeleteReason());

            infoRepList.add(infoRep);
        }

        return R.ok().putList(infoRepList);
    }
}
