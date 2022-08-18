package io.renren.modules.act.mo.request;

import io.renren.common.validator.group.UpdateGroup;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 *
 * @Date 2020/9/22 10:44
 */
@Data
public class ActReModelUpdateReq implements Serializable {
    @NotBlank(message = "id不能为空", groups = UpdateGroup.class)
    private String id;
    @NotBlank(message = "名称不能为空", groups = UpdateGroup.class)
    private String name;
    @NotBlank(message = "标识不能为空", groups = UpdateGroup.class)
    private String key;
    private String description;
}
