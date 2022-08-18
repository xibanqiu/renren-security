package io.renren.modules.act.mo.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 *
 * @Date 2020/7/23 13:37
 */
@Data
public class ActReProcDefResourceReq implements Serializable {
    @NotBlank(message = "部署Id为空")
    private String deploymentId;
    @NotBlank(message = "资源名称为空")
    private String resourceName;
}
