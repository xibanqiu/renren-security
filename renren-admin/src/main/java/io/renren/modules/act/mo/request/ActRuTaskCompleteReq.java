package io.renren.modules.act.mo.request;

import lombok.Data;

import java.io.Serializable;

/**
 *
 * @Date 2020/7/23 16:48
 */
@Data
public class ActRuTaskCompleteReq implements Serializable {
    private String taskId;
    private String procInstanceId;
    private String outcome;
    private String nodeType;
    private String comment;
    private Long patentCaseId;
}
