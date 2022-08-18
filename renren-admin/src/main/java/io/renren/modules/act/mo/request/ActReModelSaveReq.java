package io.renren.modules.act.mo.request;

import io.renren.common.validator.group.AddGroup;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 *
 * @Date 2020/7/21 13:35
 */
@Data
public class ActReModelSaveReq implements Serializable {
    @NotBlank(message = "名称不能为空", groups = AddGroup.class)
    private String name;
    @NotBlank(message = "标识不能为空", groups = AddGroup.class)
    private String key;
    private String description;
}
