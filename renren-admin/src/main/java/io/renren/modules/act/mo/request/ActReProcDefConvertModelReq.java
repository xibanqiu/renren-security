package io.renren.modules.act.mo.request;

import lombok.Data;

import java.io.Serializable;

/**
 *
 * @Date 2020/9/10 14:52
 */
@Data
public class ActReProcDefConvertModelReq implements Serializable {
    private String processDefinitionId;
}
