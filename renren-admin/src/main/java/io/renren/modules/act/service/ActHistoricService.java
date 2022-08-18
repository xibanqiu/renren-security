package io.renren.modules.act.service;

import io.renren.common.utils.PageUtils;
import io.renren.modules.act.mo.query.ActHiProcInstanceQuery;
import io.renren.modules.act.mo.query.ActHiTaskPageQuery;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricTaskInstance;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *
 * @Date 2020/7/23 18:26
 */
public interface ActHistoricService {

    /**
     * 获取流程实例 (最近的)
     * @param query
     * @return
     */
    HistoricProcessInstance fetchHistoricLastProcInstance(ActHiProcInstanceQuery query);

    List<HistoricProcessInstance> fetchHistoricLastProcInstanceList(Set<String> procInstanceBusinessKeySet);

    /**
     * 获取所有流程信息 只是父流程
     * @param procInstanceBusinessKeySet
     * @return
     */
    Map<String, HistoricProcessInstance> fetchHistoricLastProcInstanceMap(Set<String> procInstanceBusinessKeySet);

    /**
     * 获取所有流程信息 包括子流程
     * @param procInstanceBusinessKeySet
     * @return
     */
    Map<String, List<HistoricProcessInstance>> fetchHistoricLastAllProcInstanceMap(Set<String> procInstanceBusinessKeySet);

    /**
     * 查看完成的任务 分页
     * @param query
     * @return
     */
    PageUtils<HistoricTaskInstance> fetchHistoricTaskPage(ActHiTaskPageQuery query);

    /**
     * 获取流程实例
     * @param processInstanceIds
     * @return
     */
    List<HistoricProcessInstance> fetchHistoricProcInstanceList(Set<String> processInstanceIds);

    HistoricProcessInstance fetchHistoricProcInstanceById(String processInstanceId);

    List<HistoricProcessInstance> fetchHistoricProcInstanceBySuperId(String superProcessInstanceId);

    /**
     * 获取未完成的子流程实例
     * @param superProcessInstanceId
     * @return
     */
    List<HistoricProcessInstance> fetchUnfinishedHistoricProcInstanceBySuperId(String superProcessInstanceId);
    /**
     * 获取已完成的子流程实例
     * @param superProcessInstanceId
     * @return
     */
    List<HistoricProcessInstance> fetchFinishedHistoricProcInstanceBySuperId(String superProcessInstanceId);

    /**
     * 包括子流程
     * @param processInstanceId
     * @return
     */
    List<HistoricProcessInstance> fetchHistoricProcInstanceAllListById(String processInstanceId);

    /**
     * 获取流程实例 业务key
     * @param procInstanceIdSet
     * @return
     */
    Map<String, String> fetchProcInstanceBusinessKey(Set<String> procInstanceIdSet);
}
