package io.renren.modules.act.service;

import io.renren.common.utils.PageUtils;
import io.renren.modules.act.mo.query.ActHiActivityListQuery;
import io.renren.modules.act.mo.query.ActHiActivityPageQuery;
import org.activiti.engine.history.HistoricActivityInstance;

import java.util.List;

/**
 *
 * @Date 2020/10/30 15:29
 */
public interface ActHiActivityService {
    PageUtils<HistoricActivityInstance> fetchActHisActivityPage(ActHiActivityPageQuery query);


    List<HistoricActivityInstance> fetchActHisActivityList(ActHiActivityListQuery query);

}
