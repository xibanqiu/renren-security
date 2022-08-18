package io.renren.modules.act.mo.response;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 *
 * @Date 2020/7/21 15:19
 */
@Data
public class ActReDeploymentInfoRep implements Serializable {
    private String procDefId;
    private String deploymentId;
    private String name;
    private String key;
    private Integer version;
    private String resourceName;
    private String dgrmResourceName;
    private Date deployTime;
}
