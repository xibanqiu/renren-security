package io.renren.modules.act.mo.query;

import io.renren.common.model.PageQueryInfo;
import lombok.Data;

/**
 *
 * @Date 2020/10/30 15:36
 */
@Data
public class ActHiActivityPageQuery extends PageQueryInfo {
    private String procDefinitionId;
    private String procActivityId;
    private boolean finished;
}
