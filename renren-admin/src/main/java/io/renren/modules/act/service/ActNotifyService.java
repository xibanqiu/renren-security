package io.renren.modules.act.service;

/**
 *
 * @Date 2020/9/7 13:37
 */
public interface ActNotifyService {
    void iprNotifyBySms(String procTagName, String rootProcessInstanceId, String procBusinessKey, String taskName, String assignee);
}
