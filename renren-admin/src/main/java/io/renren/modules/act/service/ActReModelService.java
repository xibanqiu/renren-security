package io.renren.modules.act.service;

import io.renren.common.utils.PageUtils;
import org.activiti.engine.runtime.ProcessInstance;

import java.util.List;
import java.util.Map;

/**
 * 
 *
 * @author ywz
 * @email weizhao.yao@yintech.cn
 * @date 2020-07-20 18:14:04
 */
public interface ActReModelService {

    PageUtils queryPage(Map<String, Object> params);

    Map<String, String> getActivityNameByProcessInstance(List<ProcessInstance> processInstanceList);


}

