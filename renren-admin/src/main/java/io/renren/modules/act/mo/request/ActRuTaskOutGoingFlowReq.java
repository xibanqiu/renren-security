package io.renren.modules.act.mo.request;

import lombok.Data;

import java.io.Serializable;

/**
 *
 * @Date 2020/7/22 18:19
 */
@Data
public class ActRuTaskOutGoingFlowReq implements Serializable {
    private String taskId;
    private String taskDefineKey;
    private String procDefineId;
}
