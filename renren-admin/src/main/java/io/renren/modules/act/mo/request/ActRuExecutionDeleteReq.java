package io.renren.modules.act.mo.request;

import lombok.Data;

import java.io.Serializable;

/**
 *
 * @Date 2020/8/5 11:37
 */
@Data
public class ActRuExecutionDeleteReq implements Serializable {
    private String procInstanceId;
}
