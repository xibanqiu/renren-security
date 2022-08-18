package io.renren.modules.act.service.impl;

import io.renren.common.utils.PageUtils;
import io.renren.modules.act.service.ActReDeploymentService;
import org.springframework.stereotype.Service;

import java.util.Map;


@Service("actReDeploymentService")
public class ActReDeploymentServiceImpl implements ActReDeploymentService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {

        return new PageUtils(null);
    }

}