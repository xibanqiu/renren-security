package io.renren.modules.act.mo.response;

import lombok.Data;

import java.io.Serializable;

/**
 *
 * @Date 2020/7/29 14:23
 */
@Data
public class IprProcAuditToOARep implements Serializable {
    private Long code;
    private Integer count;
    private String respCode;
    private String msg;
    private String stacktrace;
    private boolean success;
    private OARepInfo data;

    @Data
    public static class OARepInfo implements Serializable {
        private Long requestid;
    }


    public Long getRequestId() {
        if (this.getCode().equals(0L)) {
            return this.getData().getRequestid();
        }

        return null;
    }
}
