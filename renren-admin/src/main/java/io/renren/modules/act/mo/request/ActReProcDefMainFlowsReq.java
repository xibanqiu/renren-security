package io.renren.modules.act.mo.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 *
 * @Date 2020/11/2 10:05
 */
@Data
public class ActReProcDefMainFlowsReq implements Serializable {
    @NotBlank(message = "定义Id为空")
    private String procDefinitionId;
}
