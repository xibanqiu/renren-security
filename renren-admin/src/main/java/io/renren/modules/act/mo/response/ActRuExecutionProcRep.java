package io.renren.modules.act.mo.response;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 *
 * @Date 2020/8/5 9:27
 */
@Data
public class ActRuExecutionProcRep implements Serializable {
    private String procInstanceId;
    private String procDefinitionKey;
    private String procDefinitionId;
    private String procDefinitionName;
    private String businessKey;
    private String activityName;
    private boolean suspended;
    private Date startTime;
}
