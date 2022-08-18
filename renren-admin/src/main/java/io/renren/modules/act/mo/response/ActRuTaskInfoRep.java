package io.renren.modules.act.mo.response;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 *
 * @Date 2020/7/22 17:17
 */
@Data
public class ActRuTaskInfoRep implements Serializable {
    private String id;
    private String name;
    private String assignee;
    private String procDefId;
    private Date createTime;
    private Date dueDate;
    private Date claimTime;
}
