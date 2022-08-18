package io.renren.modules.act.mo.response;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 *
 * @Date 2020/10/29 9:40
 */
@Data
public class ActHisProcessInfoRep implements Serializable {

    private String procInstanceId;
    private String procDefinitionName;
    private String procDefinitionNameCn;
    private Date startTime;
    private Date endTime;
    private Long durationInMillis;
    private String deleteReason;

    private List<ActHisActivityInfoRep> dealActivityList;

    @Data
    public static class ActHisActivityInfoRep implements Serializable {
        private String name;
        private String documentation;
        private String assignee;
    }
}
