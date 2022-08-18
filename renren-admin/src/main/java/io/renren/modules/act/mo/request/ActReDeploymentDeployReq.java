package io.renren.modules.act.mo.request;

import io.renren.common.validator.group.AddGroup;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 *
 * @Date 2020/7/21 15:30
 */
@Data
public class ActReDeploymentDeployReq implements Serializable {
    @NotBlank(message = "模型Id不能为空", groups = AddGroup.class)
    private String modelId;
}
