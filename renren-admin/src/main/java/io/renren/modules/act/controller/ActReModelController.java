package io.renren.modules.act.controller;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.Lists;
import io.renren.common.annotation.SysLog;
import io.renren.common.exception.RRException;
import io.renren.common.utils.PageUtils;
import io.renren.common.utils.R;
import io.renren.common.validator.ValidatorUtils;
import io.renren.common.validator.group.AddGroup;
import io.renren.common.validator.group.UpdateGroup;
import io.renren.modules.act.mo.convert.ActInfoConvert;
import io.renren.modules.act.mo.request.ActReModelPageReq;
import io.renren.modules.act.mo.request.ActReModelSaveReq;
import io.renren.modules.act.mo.request.ActReModelUpdateReq;
import io.renren.modules.act.mo.response.ActReModelInfoRep;
import org.activiti.bpmn.converter.BpmnXMLConverter;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.bpmn.model.Process;
import org.activiti.editor.constants.ModelDataJsonConstants;
import org.activiti.editor.language.json.converter.BpmnJsonConverter;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.repository.Model;
import org.apache.commons.io.IOUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;

import static org.activiti.editor.constants.ModelDataJsonConstants.MODEL_DESCRIPTION;
import static org.activiti.editor.constants.ModelDataJsonConstants.MODEL_NAME;

/**
 *
 * @Date 2020/7/20 18:00
 */
@RestController()
@RequestMapping(value = "/act/reModel")
public class ActReModelController {
    private static final Logger logger = LoggerFactory.getLogger(ModelerController.class);


    @Resource
    private ObjectMapper objectMapper;
    @Autowired
    private RepositoryService repositoryService;

    /**
     * 列表
     */
 //   @SysLog(value = "流程模型列表 分页")
    @RequestMapping("/list")
//    @RequiresPermissions("act:remodel:list")
    public R list(HttpServletRequest request, ActReModelPageReq req){

        List<Model> modelList = this.repositoryService.createModelQuery()
                .orderByCreateTime().desc()
                .orderByModelVersion().desc()
                .listPage(req.getLimit() * (req.getPage() - 1), req.getLimit());

        Long count = this.repositoryService.createModelQuery().count();
        List<ActReModelInfoRep> modelInfoRepList = ActInfoConvert.convertActModelDBInfoToRep(modelList);

        PageUtils page = new PageUtils(modelInfoRepList, count.intValue(), req.getLimit(), req.getPage());

        return R.ok().put("page", page);
    }

    /**
     * 模型信息
     */
    @GetMapping("/info/{modelId}")
//    @RequiresPermissions("act:remodel:info")
    public R info(@PathVariable("modelId") String modelId){
        Model model = this.repositoryService.getModel(modelId);
        if (null == model) {
            return R.ok();
        }
        List<ActReModelInfoRep> modelInfoRepList = ActInfoConvert.convertActModelDBInfoToRep(Lists.newArrayList(model));

        return R.ok().putData(modelInfoRepList.get(0));
    }


    /**
     * 保存模型
     */
    @SysLog("保存模型")
    @PostMapping("/save")
    public R save(@RequestBody ActReModelSaveReq saveReq){
        ValidatorUtils.validateEntity(saveReq, AddGroup.class);

        ObjectNode modelNode = objectMapper.createObjectNode();
        modelNode.put(MODEL_NAME, saveReq.getName());
        modelNode.put(ModelDataJsonConstants.MODEL_REVISION, 1);
        modelNode.put(MODEL_DESCRIPTION, saveReq.getDescription());

        Model model = this.repositoryService.newModel();
        model.setName(saveReq.getName());
        model.setKey(saveReq.getKey());
        model.setMetaInfo(modelNode.toString());

        this.repositoryService.saveModel(model);

        this.perfectModelEditorSource(model.getId());

        return R.ok();
    }

    @SysLog("更新模型")
    @PostMapping("/update")
    public R update(@RequestBody ActReModelUpdateReq req){
        ValidatorUtils.validateEntity(req, UpdateGroup.class);

        Model model = this.repositoryService.getModel(req.getId());
        model.setName(req.getName());
        model.setKey(req.getKey());
        try {
            ObjectNode modelJson = (ObjectNode) objectMapper.readTree(model.getMetaInfo());
            modelJson.put(MODEL_NAME, req.getName());
            modelJson.put(MODEL_DESCRIPTION, req.getDescription());
            model.setMetaInfo(modelJson.toString());
        }catch (Exception e)
        {
            logger.info("metaInfo 转化异常：{}",e.getCause());
        }
        this.repositoryService.saveModel(model);
        return R.ok();
    }

    @SysLog(value = "导出模型")
    @RequestMapping(value = "/export/{modelId}")
    @ResponseBody
    public void exportModelXml(@PathVariable("modelId") String modelId, HttpServletResponse response) throws IOException {

        byte[] bytes = repositoryService.getModelEditorSource(modelId);

        BpmnJsonConverter jsonConverter = new BpmnJsonConverter();
        JsonNode editorNode = new ObjectMapper().readTree(bytes);
        BpmnModel bpmnModel = jsonConverter.convertToBpmnModel(editorNode);
        Process process = bpmnModel.getMainProcess();
        if (null == process) {
            throw new RRException("模型xml不存在");
        }
        BpmnXMLConverter xmlConverter = new BpmnXMLConverter();
        byte[] bpmnBytes = xmlConverter.convertToXML(bpmnModel);

        ByteArrayInputStream in = new ByteArrayInputStream(bpmnBytes);
        String filename = bpmnModel.getMainProcess().getId() + ".bpmn20.xml";
        response.setContentType("application/xml");
        response.setHeader("Content-Disposition", "attachment; filename=" + filename);
        IOUtils.copy(in, response.getOutputStream());//这句必须放到setHeader下面，否则10K以上的xml无法导出，
        response.flushBuffer();

    }

    /**
     * 创建模型时完善ModelEditorSource
     * @param modelId
     */
    private void perfectModelEditorSource(String modelId){
        logger.info("perfectModelEditorSource modelId：{}", modelId);
        ObjectNode editorNode = objectMapper.createObjectNode();
        editorNode.put("id", "canvas");
        editorNode.put("resourceId", "canvas");
        ObjectNode stencilSetNode = objectMapper.createObjectNode();
        stencilSetNode.put("namespace","http://b3mn.org/stencilset/bpmn2.0#");
        editorNode.put("stencilset", stencilSetNode);
        try {
            repositoryService.addModelEditorSource(modelId,editorNode.toString().getBytes("utf-8"));
        } catch (Exception e) {
            logger.error("perfectModelEditorSource ",e);
        }
        logger.info("perfectModelEditorSource end");
    }
}
