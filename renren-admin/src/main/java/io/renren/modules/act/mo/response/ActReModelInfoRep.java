package io.renren.modules.act.mo.response;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 *
 * @Date 2020/7/21 11:07
 */
@Data
public class ActReModelInfoRep implements Serializable {
    private String id;
    private String name;
    private String key;
    private Integer version;
    private Date createTime;
    private Date lastUpdateTime;
    private String description;

}
