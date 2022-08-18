package io.renren.modules.act.controller;

import io.renren.common.annotation.SysLog;
import io.renren.common.utils.R;
import io.renren.common.validator.ValidatorUtils;
import io.renren.common.validator.group.AddGroup;
import io.renren.modules.act.mo.request.ActReDeploymentDeployReq;
import io.renren.modules.act.mo.request.ActReDeploymentPageReq;
import io.renren.modules.act.service.ActReDeploymentService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.bpmn.model.Process;
import org.activiti.editor.language.json.converter.BpmnJsonConverter;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.Model;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;


/**
 * 
 *
 * @author ywz
 * @email weizhao.yao@yintech.cn
 * @date 2020-07-21 15:10:18
 */
@RestController
@RequestMapping("act/actredeployment")
public class ActReDeploymentController {
    private static final Logger logger = LoggerFactory.getLogger(ActReDeploymentController.class);

    @Autowired
    private ActReDeploymentService actReDeploymentService;
    @Autowired
    private RepositoryService repositoryService;

    /**
     * 列表
     */
    @SysLog("部署信息 分页")
    @RequestMapping("/list")
    @RequiresPermissions("act:actredeployment:list")
    public R list(ActReDeploymentPageReq req){

        return R.ok().put("page", null);
    }

    /**
     * 部署流程模型
     */
    @SysLog("部署流程")
    @RequestMapping("/deploy")
//    @RequiresPermissions("act:actredeployment:deploy")
    public R deploy(@RequestBody ActReDeploymentDeployReq req){
        ValidatorUtils.validateEntity(req, AddGroup.class);

        try {
            Model modelData = repositoryService.getModel(req.getModelId());
            byte[] bytes = repositoryService.getModelEditorSource(modelData.getId());
            if (bytes == null) {
                logger.info("deployID:{} model is null", req.getModelId());
                return R.error("模型为空 不能部署");
            }
            JsonNode modelNode = new ObjectMapper().readTree(bytes);
            BpmnModel bpmnModel = new BpmnJsonConverter().convertToBpmnModel(modelNode);
            Process process = bpmnModel.getMainProcess();
            if (process == null) {
                return R.error("模型为空 不能部署");
            }
            //此处以模型key为准
            process.setId(modelData.getKey());
            process.setName(modelData.getName());

            Deployment deployment = repositoryService.createDeployment()
                    .name(modelData.getName())
                    .key(modelData.getKey())
                    .category(modelData.getCategory())
                    .tenantId(modelData.getTenantId())
                    .addBpmnModel(modelData.getKey()+".bpmn20.xml", bpmnModel)
                    .deploy();

            modelData.setDeploymentId(deployment.getId());
            repositoryService.saveModel(modelData);

            return R.ok();
        } catch (Exception e) {
            logger.error("deploy ", e);
        }

        return R.error();
    }


    /**
     * 修改
     */
    @RequestMapping("/update")
//    @RequiresPermissions("act:actredeployment:update")
    public R update(){
//		actReDeploymentService.updateById(actReDeployment);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
//    @RequiresPermissions("act:actredeployment:delete")
    public R delete(@RequestBody String[] ids){
//		actReDeploymentService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
