package io.renren.modules.act.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import io.renren.common.annotation.SysLog;
import io.renren.common.exception.RRException;
import io.renren.common.utils.PageUtils;
import io.renren.common.utils.R;
import io.renren.common.validator.ValidatorUtils;
import io.renren.modules.act.mo.convert.ActInfoConvert;
import io.renren.modules.act.mo.request.ActReProcDefConvertModelReq;
import io.renren.modules.act.mo.request.ActReProcDefMainFlowsReq;
import io.renren.modules.act.mo.request.ActReProcDefPageReq;
import io.renren.modules.act.mo.request.ActReProcDefResourceReq;
import io.renren.modules.act.mo.response.ActReDeploymentInfoRep;
import io.renren.modules.act.mo.response.ActReProcDefMainFlowsRep;
import io.renren.modules.act.service.ActReProcdefService;
import lombok.extern.slf4j.Slf4j;
import org.activiti.bpmn.converter.BpmnXMLConverter;
import org.activiti.bpmn.model.*;
import org.activiti.bpmn.model.Process;
import org.activiti.editor.constants.ModelDataJsonConstants;
import org.activiti.editor.language.json.converter.BpmnJsonConverter;
import org.activiti.engine.ProcessEngineConfiguration;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.Model;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.repository.ProcessDefinitionQuery;
import org.activiti.image.ProcessDiagramGenerator;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;


/**
 * 
 *
 * @author ywz
 * @email weizhao.yao@yintech.cn
 * @date 2020-07-21 16:13:17
 */
@Slf4j
@RestController
@RequestMapping("act/actreprocdef")
public class ActReProcdefController {
    @Autowired
    private ActReProcdefService actReProcdefService;
    @Autowired
    private RepositoryService repositoryService;
    @Autowired
    private ProcessEngineConfiguration processEngineConfiguration;
    /**
     * 列表
     */
    @SysLog(value = "流程定义列表 分页")
    @RequestMapping("/list")
//    @RequiresPermissions("act:actreprocdef:list")
    public R list(ActReProcDefPageReq req){

        ProcessDefinitionQuery processDefinitionQuery = this.repositoryService.createProcessDefinitionQuery();

        if(StringUtils.isNotBlank(req.getKey())){
            processDefinitionQuery = processDefinitionQuery.processDefinitionKey(req.getKey());
        }

        List<ProcessDefinition> definitionList = processDefinitionQuery

                .orderByProcessDefinitionKey().asc()
                .orderByProcessDefinitionVersion().desc()
                .listPage(req.getLimit() * (req.getPage() - 1), req.getLimit());

        Long count = processDefinitionQuery.count();


        Set<String> deployIdSet = definitionList.stream().map(p->{return p.getDeploymentId();}).collect(Collectors.toSet());
        Map<String, Deployment> deploymentMap = Maps.newHashMap();
        for (String deployId : deployIdSet) {
            Deployment deployment = this.repositoryService.createDeploymentQuery().deploymentId(deployId).singleResult();
            deploymentMap.put(deployId, deployment);
        }

        List<ActReDeploymentInfoRep> infoRepList = ActInfoConvert.convertActDeploymentDBInfoToRep(
                definitionList, deploymentMap);

        PageUtils page = new PageUtils(infoRepList, count.intValue(), req.getLimit(), req.getPage());

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    @RequiresPermissions("act:actreprocdef:info")
    public R info(@PathVariable("id") String id){
//		ActReProcdefEntity actReProcdef = actReProcdefService.getById(id);
//
//        return R.ok().put("actReProcdef", actReProcdef);
        return null;
    }

    @RequestMapping(value = "process/definition/list")
    public void fetchProcessDefInfo() {
        return;
    }

    @SysLog(value = "流程定义 任务类型节点")
    @RequestMapping(value = "process/definition/main/task/flowList")
    public R fetchProcessMainDefFlows(ActReProcDefMainFlowsReq req) {
        ValidatorUtils.validateEntity(req);
        List<ActReProcDefMainFlowsRep> repList = Lists.newArrayList();
        List<FlowElement> flowElementList = this.actReProcdefService.fetchMainProcBpmnFlowElements(req.getProcDefinitionId());
        for (FlowElement flowElement : flowElementList) {
            if (flowElement instanceof  UserTask
                    || flowElement instanceof ReceiveTask
                    || flowElement instanceof CallActivity
            ) {
                ActReProcDefMainFlowsRep flowsRep = new ActReProcDefMainFlowsRep();
                flowsRep.setId(flowElement.getId());
                flowsRep.setName(flowElement.getName());
                repList.add(flowsRep);
            }
        }

        return R.ok().putList(repList);
    }

    @SysLog("流程定义资源预览")
    @RequestMapping(value = "/process/resourceView")
    public void processResourceView(ActReProcDefResourceReq req, HttpServletResponse response) throws IOException {

        ValidatorUtils.validateEntity(req);
        String deploymentId = req.getDeploymentId();
        String resourceName = req.getResourceName();


        if (resourceName.contains(".xml")) {
            InputStream inputStream = this.repositoryService.getResourceAsStream(deploymentId, resourceName);
            if (inputStream == null) {
                throw new RRException("未找到资源");
            }

            response.setContentType("application/xml;charset=utf-8");
            IOUtils.copy(inputStream, response.getOutputStream());//这句必须放到setHeader下面，否则10K以上的xml无法导出，
            response.flushBuffer();
        }
        else {

            ProcessDefinition processDefinition = this.repositoryService.createProcessDefinitionQuery().deploymentId(deploymentId).singleResult();
//            InputStream inputStream = this.repositoryService.getProcessDiagram(processDefinition.getId());

            BpmnModel bpmnModel = this.repositoryService.getBpmnModel(processDefinition.getId());
            ProcessDiagramGenerator processDiagramGenerator = processEngineConfiguration.getProcessDiagramGenerator();
            InputStream inputStream = processDiagramGenerator.generateDiagram(bpmnModel, "png",
                    Collections.emptyList(),
                    Collections.emptyList(),
                    "微软雅黑",
                    "微软雅黑",
                    "微软雅黑",
                    null,
                    1.0D);

            response.setContentType("image/png;charset=utf-8");
            IOUtils.copy(inputStream, response.getOutputStream());
            response.flushBuffer();
        }
    }

    /**
     * 导入模型文件
     * @param name
     * @param key
     * @param file
     * @return
     */
    @SysLog(value = "导入流程文件", saveDB = false)
    @RequestMapping(value = "/importProcessFile", method = { RequestMethod.POST })
    public R importProcessFile(
            @RequestParam(name = "name", required = true) String name,
            @RequestParam(name = "key", required = true) String key,
            @RequestParam(name = "file", required = true) MultipartFile file) {
        try {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(file.getBytes());

            XMLInputFactory xif = XMLInputFactory.newInstance();
            InputStreamReader in = new InputStreamReader(inputStream, "UTF-8");
            XMLStreamReader xtr = xif.createXMLStreamReader(in);

            BpmnXMLConverter xmlConverter = new BpmnXMLConverter();
            BpmnModel bpmnModel = xmlConverter.convertToBpmnModel(xtr);
            Process process = bpmnModel.getMainProcess();
            if (process == null) {
                return R.error("模型为空 不能部署");
            }
            //此处以模型key为准
            process.setId(key);
            process.setName(name);

            Deployment deployment = repositoryService.createDeployment()
                    .name(name)
                    .key(key)
                    .addBpmnModel(key+".bpmn20.xml", bpmnModel)
                    .deploy();

            log.info("importProcessFile {}", deployment.getId());
        }
        catch (Throwable e) {
            log.error("importProcessFile", e);
            return R.error();
        }
        return R.ok();
    }

    /**
     * 将流程定义转换为模型
     * @param req
     * @return
     */
    @RequestMapping(value = "/convertToModel")
    public R convertToModel(@RequestBody ActReProcDefConvertModelReq req) {
        try {
            ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
                    .processDefinitionId(req.getProcessDefinitionId()).singleResult();
            InputStream bpmnStream = repositoryService.getResourceAsStream(processDefinition.getDeploymentId(),
                    processDefinition.getResourceName());
            XMLInputFactory xif = XMLInputFactory.newInstance();
            InputStreamReader in = new InputStreamReader(bpmnStream, "UTF-8");
            XMLStreamReader xtr = xif.createXMLStreamReader(in);
            BpmnModel bpmnModel = new BpmnXMLConverter().convertToBpmnModel(xtr);

            BpmnJsonConverter converter = new BpmnJsonConverter();
            com.fasterxml.jackson.databind.node.ObjectNode modelNode = converter.convertToJson(bpmnModel);
            Model modelData = repositoryService.newModel();
            modelData.setKey(processDefinition.getKey());
            modelData.setName(processDefinition.getName());
            modelData.setCategory(processDefinition.getDeploymentId());

            ObjectNode modelObjectNode = new ObjectMapper().createObjectNode();
            modelObjectNode.put(ModelDataJsonConstants.MODEL_NAME, processDefinition.getName());
            modelObjectNode.put(ModelDataJsonConstants.MODEL_REVISION, 1);
            modelObjectNode.put(ModelDataJsonConstants.MODEL_DESCRIPTION, processDefinition.getDescription());
            modelData.setMetaInfo(modelObjectNode.toString());
            repositoryService.saveModel(modelData);
            repositoryService.addModelEditorSource(modelData.getId(), modelNode.toString().getBytes("utf-8"));
            return R.ok();
        }
        catch (Throwable throwable) {
            log.info("convertToModel ", throwable);
            return R.error();
        }
    }
}
