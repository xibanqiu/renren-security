package io.renren.modules.act.mo.query;

import io.renren.common.model.PageQueryInfo;
import lombok.Data;

import java.util.List;

/**
 *
 * @Date 2020/7/23 18:28
 */
@Data
public class ActHiTaskPageQuery extends PageQueryInfo {
    private List<String> processDefinitionKeyList;
    private String businessKeyLike;
    private String assignee;
    private String processNode;
    private String processInstanceBusinessKey;
    private List<String> assigneeList;
}
