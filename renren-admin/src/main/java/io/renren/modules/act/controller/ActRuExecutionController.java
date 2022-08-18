package io.renren.modules.act.controller;


import io.renren.common.annotation.SysLog;
import io.renren.common.utils.PageUtils;
import io.renren.common.utils.R;
import io.renren.common.validator.ValidatorUtils;
import io.renren.modules.act.mo.convert.ActInfoConvert;
import io.renren.modules.act.mo.query.ActRuProcessPageQuery;
import io.renren.modules.act.mo.request.ActRuExecutionDeleteReq;
import io.renren.modules.act.mo.request.ActRuExecutionImgReq;
import io.renren.modules.act.mo.request.ActRuExecutionProcPageReq;
import io.renren.modules.act.mo.request.ActRuExecutionStartReq;
import io.renren.modules.act.mo.response.ActRuExecutionProcRep;
import io.renren.modules.act.service.ActReModelService;
import io.renren.modules.act.service.ActRuExecutionService;
import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.runtime.ProcessInstance;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.util.List;

/**
 *
 * @Date 2020/7/22 16:12
 */
@Slf4j
@RestController
@RequestMapping("act/actruexecution")
public class ActRuExecutionController {
    @Autowired
    private RuntimeService runtimeService;
    @Autowired
    private ActReModelService actReModelService;
    @Autowired
    private ActRuExecutionService actRuExecutionService;

    @SysLog("执行中的流程 分页")
    @RequestMapping("/page")
    public R processInfoPage(ActRuExecutionProcPageReq req) {
        ActRuProcessPageQuery query = new ActRuProcessPageQuery();
        BeanUtils.copyProperties(req, query);

        PageUtils pageUtils = this.actRuExecutionService.fetchRuProcessPage(query);

        List<ActRuExecutionProcRep> procRepList = ActInfoConvert.convertProcessInstanceDBToRep(
                pageUtils.getList(),
                null);

        pageUtils.setList(procRepList);
        return R.ok().put("page", pageUtils);
    }

    @SysLog("执行中的流程 发起流程")
    @RequestMapping("/startProcess")
    public R startProcess(@RequestBody ActRuExecutionStartReq req) {
        ValidatorUtils.validateEntity(req);

        Long busId = req.getBusId();
        String procDefKey = req.getProcDefKey();
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(procDefKey);


        return R.ok();
    }

    @SysLog("删除流程")
    @RequestMapping("/deleteProcess")
    public R deleteProcess(@RequestBody ActRuExecutionDeleteReq req) {
        ValidatorUtils.validateEntity(req);

        this.runtimeService.deleteProcessInstance(req.getProcInstanceId(), "手动删除");

        return R.ok();
    }

    @SysLog("执行中的流程 流程图预览")
    @RequestMapping(value = "/process/imgView")
    public void processImgView(ActRuExecutionImgReq req, HttpServletResponse response) {

        try {
            InputStream inputStream = this.actRuExecutionService.fetchProcessImg(req.getProcInstanceId());
            response.setContentType("image/png");
            IOUtils.copy(inputStream, response.getOutputStream());
            response.flushBuffer();
        }
        catch (Throwable e) {
            log.error("processImgView", e);
        }

    }
}
