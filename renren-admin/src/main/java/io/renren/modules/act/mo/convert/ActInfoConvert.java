package io.renren.modules.act.mo.convert;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.Lists;
import io.renren.modules.act.mo.response.*;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.Model;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Comment;

import java.util.List;
import java.util.Map;

/**
 *
 * @Date 2020/7/21 11:32
 */
public class ActInfoConvert {


    public static   List<ActReModelInfoRep> convertActModelDBInfoToRep(List<Model> modelList)  {
        List<ActReModelInfoRep> modelInfoRepList = Lists.newArrayList();
        for (Model model : modelList) {
            ActReModelInfoRep infoRep = new ActReModelInfoRep();
            infoRep.setId(model.getId());
            infoRep.setName(model.getName());
            infoRep.setKey(model.getKey());
            infoRep.setVersion(model.getVersion());
            infoRep.setCreateTime(model.getCreateTime());
            infoRep.setLastUpdateTime(model.getLastUpdateTime());

            ObjectMapper objectMapper= new ObjectMapper();
            try{
                ObjectNode modelJson = (ObjectNode) objectMapper.readTree(model.getMetaInfo());
                model.setMetaInfo(modelJson.toString());
                String description = modelJson.get("description").toString();
                infoRep.setDescription(description.substring(1,description.length()-1));
            }catch (Exception e)
            {
                e.printStackTrace();
            }
            modelInfoRepList.add(infoRep);
        }

        return modelInfoRepList;
    }

    public static List<ActReDeploymentInfoRep> convertActDeploymentDBInfoToRep(
            List<ProcessDefinition> definitionList,
            Map<String, Deployment> deploymentMap ) {
        List<ActReDeploymentInfoRep> deploymentInfoRepList = Lists.newArrayList();
        for (ProcessDefinition definition : definitionList) {
            ActReDeploymentInfoRep infoRep = new ActReDeploymentInfoRep();
            infoRep.setProcDefId(definition.getId());
            infoRep.setDeploymentId(definition.getDeploymentId());
            infoRep.setName(definition.getName());
            infoRep.setKey(definition.getKey());
            infoRep.setVersion(definition.getVersion());
            infoRep.setResourceName(definition.getResourceName());
            infoRep.setDgrmResourceName(definition.getDiagramResourceName());

            Deployment deployment = deploymentMap.get(definition.getDeploymentId());
            infoRep.setDeployTime(deployment == null ? null : deployment.getDeploymentTime());

            deploymentInfoRepList.add(infoRep);
        }

        return deploymentInfoRepList;
    }


    public static List<ActRuTaskCommentsRep> convertTaskCommentsDBToRep(List<Comment> commentList) {
        List<ActRuTaskCommentsRep> repList = Lists.newArrayList();
        for (Comment comment : commentList) {
            ActRuTaskCommentsRep commentsRep = new ActRuTaskCommentsRep();
            commentsRep.setId(comment.getId());
            commentsRep.setUserId(comment.getUserId());
            commentsRep.setTime(comment.getTime());
            commentsRep.setFullMessage(comment.getFullMessage());
            repList.add(commentsRep);
        }

        return repList;
    }

    public static List<ActRuExecutionProcRep> convertProcessInstanceDBToRep(
            List<ProcessInstance> processInstanceList,
            Map<String, String> activityNameMap) {
        List<ActRuExecutionProcRep> procRepList = Lists.newArrayList();

        for (ProcessInstance processInstance : processInstanceList) {
            ActRuExecutionProcRep procRep = new ActRuExecutionProcRep();
            procRep.setProcInstanceId(processInstance.getProcessInstanceId());
            procRep.setProcDefinitionKey(processInstance.getProcessDefinitionKey());
            procRep.setProcDefinitionId(processInstance.getProcessDefinitionId());
            procRep.setProcDefinitionName(processInstance.getProcessDefinitionName());
            procRep.setBusinessKey(processInstance.getBusinessKey());
//            String activityName = activityNameMap.get(processInstance.getActivityId());
//            procRep.setActivityName(activityName);
            procRep.setSuspended(processInstance.isSuspended());
            procRep.setStartTime(processInstance.getStartTime());

            procRepList.add(procRep);
        }

        return procRepList;
    }


    public static List<ActHisProcessInfoRep.ActHisActivityInfoRep> covertHiActivityToRep(List<HistoricActivityInstance> activityInstanceList) {
        List<ActHisProcessInfoRep.ActHisActivityInfoRep> infoRepList = Lists.newArrayList();
        for (HistoricActivityInstance historicActivityInstance : activityInstanceList) {
            ActHisProcessInfoRep.ActHisActivityInfoRep infoRep = new ActHisProcessInfoRep.ActHisActivityInfoRep();
            infoRep.setName(historicActivityInstance.getActivityName());
            infoRep.setAssignee(historicActivityInstance.getAssignee());
            infoRepList.add(infoRep);
        }
        return infoRepList;
    }

}
