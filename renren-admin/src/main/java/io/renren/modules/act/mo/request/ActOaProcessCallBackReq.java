package io.renren.modules.act.mo.request;

import lombok.Data;

import java.io.Serializable;

/**
 *
 * @Date 2020/7/29 15:00
 */
@Data
public class ActOaProcessCallBackReq implements Serializable {
    private Long requestId;
    private int state;
    private String msg;
}
