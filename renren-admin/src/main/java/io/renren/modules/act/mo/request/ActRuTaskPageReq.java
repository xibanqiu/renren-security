package io.renren.modules.act.mo.request;

import io.renren.common.model.PageQueryInfo;
import lombok.Data;

import java.util.List;

/**
 *
 * @Date 2020/7/22 17:09
 */
@Data
public class ActRuTaskPageReq extends PageQueryInfo {
    private List<String> processDefinitionKeyList;
    private String businessKey;
    private String assignee;
    private String processNode;
    private List<String> assigneeList;
    private String candidateUser;
    private String candidateGroup;
    private Boolean taskUnassigned;

}
