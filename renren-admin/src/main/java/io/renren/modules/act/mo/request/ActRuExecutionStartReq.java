package io.renren.modules.act.mo.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 *
 * @Date 2020/7/22 16:16
 */
@Data
public class ActRuExecutionStartReq implements Serializable {
    @NotNull(message = "业务Id不能为空")
    private Long busId;
    @NotBlank(message = "流程定义key不能为空")
    private String procDefKey;
}
