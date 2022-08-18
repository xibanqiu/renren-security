package io.renren.modules.act.mo.query;

import lombok.Data;

import java.io.Serializable;

/**
 *
 * @Date 2020/11/2 13:11
 */
@Data
public class ActHiActivityListQuery implements Serializable {
    private String procInstanceId;
    private boolean finished;

}
