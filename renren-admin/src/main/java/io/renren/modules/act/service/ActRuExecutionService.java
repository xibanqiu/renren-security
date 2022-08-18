package io.renren.modules.act.service;


import io.renren.common.utils.PageUtils;
import io.renren.modules.act.mo.domain.ManualFlowElement;
import io.renren.modules.act.mo.query.ActRuExecutionPageQuery;
import io.renren.modules.act.mo.query.ActRuProcessPageQuery;
import org.activiti.engine.runtime.Execution;
import org.activiti.engine.runtime.ProcessInstance;

import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *
 * @Date 2020/7/22 16:25
 */
public interface ActRuExecutionService {

    PageUtils<ProcessInstance> fetchRuProcessPage(ActRuProcessPageQuery query);

    PageUtils<Execution> fetchRuExecutionPage(ActRuExecutionPageQuery query);

    String startProcess(String procDefKey, String busId, Map<String, Object> variables);

    InputStream fetchProcessImg(String procInstanceId);

    List<ProcessInstance> fetchProcessInstance(Set<String> procInstanceIdSet);

    ProcessInstance fetchProcessInstance(String procInstanceId);

    Map<String, String> fetchProcInstanceBusinessKey(Set<String> procInstanceIdSet);

    /**
     * 写的有问题
     * @param procBusinessKeyList
     * @return
     */
    Map<String, List<ManualFlowElement>> fetchProcActivityNameMap(Set<String> procBusinessKeyList);


}
