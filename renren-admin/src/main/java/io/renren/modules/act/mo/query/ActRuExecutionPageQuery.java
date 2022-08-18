package io.renren.modules.act.mo.query;

import io.renren.common.model.PageQueryInfo;
import lombok.Data;

import java.util.Set;

/**
 *
 * @Date 2020/10/29 17:46
 */
@Data
public class ActRuExecutionPageQuery extends PageQueryInfo {
    private Set<String> procDefinitionKeySet;
}
