package io.renren.modules.act.mo.response;

import lombok.Data;

import java.io.Serializable;

/**
 *
 * @Date 2020/11/2 13:19
 */
@Data
public class ActHiActivityInfoRep implements Serializable {
    private String name;
    private String documentation;
    private String assignee;
}
