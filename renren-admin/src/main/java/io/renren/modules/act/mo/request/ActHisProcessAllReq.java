package io.renren.modules.act.mo.request;

import lombok.Data;

import java.io.Serializable;

/**
 *
 * @Date 2020/10/29 9:39
 */
@Data
public class ActHisProcessAllReq implements Serializable {
    private String procInstanceId;
}
