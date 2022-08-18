package io.renren.modules.act.mo.response;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 *
 * @Date 2020/7/23 16:20
 */
@Data
public class ActRuTaskCommentsRep implements Serializable {
    private String id;
    private String userId;
    private Date time;
    private String fullMessage;
}
