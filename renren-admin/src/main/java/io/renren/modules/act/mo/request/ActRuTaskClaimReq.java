package io.renren.modules.act.mo.request;

import lombok.Data;

import java.io.Serializable;

/**
 *
 * @Date 2020/7/31 10:27
 */
@Data
public class ActRuTaskClaimReq implements Serializable {
    private String taskId;
}
