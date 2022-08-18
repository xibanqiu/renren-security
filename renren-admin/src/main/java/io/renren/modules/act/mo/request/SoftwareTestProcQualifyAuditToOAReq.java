package io.renren.modules.act.mo.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 *
 * @Date 2020/9/2 10:24
 */
@Data
public class SoftwareTestProcQualifyAuditToOAReq implements Serializable {
    @ApiModelProperty(value = "标题")
    private String title;
    @ApiModelProperty(value = "身份证号")
    private String xqr;
    @ApiModelProperty(value = "申请类别")
    private String sqlb;
    @ApiModelProperty(value = "著作权类别")
    private String rjzzqlb;
    @ApiModelProperty(value = "需求类型 申请")
    private String ck11;
    @ApiModelProperty(value = "期望完成日期")
    private String qwwcrq;
    @ApiModelProperty(value = "完成时限要求")
    private String wcsxyq;

    @ApiModelProperty(value = "需求说明")
    private String xqsmrjzz;
    @ApiModelProperty(value = "申请目的")
    private String sqmd;
    @ApiModelProperty(value = "应用产品")
    private String yycp;
    @ApiModelProperty(value = "申请主体")
    private String sqzt;
    @ApiModelProperty(value = "关联附件")
    private String glfj;
    @ApiModelProperty(value = "关联附件名")
    private String glfjmc;
    @ApiModelProperty(value = "操作人")
    private String operatorId;




    @ApiModelProperty(value = "流程说明")
    private String lcsm;
    @ApiModelProperty(value = "流程逻辑查看")
    private String lcljck;
    @ApiModelProperty(value = "申请人")
    private String sqr;
    @ApiModelProperty(value = "申请人部门")
    private String sqrbm;
    @ApiModelProperty(value = "申请人事业部")
    private String sqrsyb;
    @ApiModelProperty(value = "需求类型")
    private String xqlx;
    @ApiModelProperty(value = "软件测试类型")
    private String rjcslx;
    @ApiModelProperty(value = "期望报告时间")
    private String qwbgsj;
    @ApiModelProperty(value = "软件测试申请主体")
    private String rjcssqzt;
    @ApiModelProperty(value = "测试软件全称")
    private String csrjqc;
    @ApiModelProperty(value = "测试软件简称")
    private String csrjjc;
    @ApiModelProperty(value = "测试软件版本号")
    private String csrjbbh;
    @ApiModelProperty(value = "测试加急类型")
    private String csjjlx;
    @ApiModelProperty(value = "附件上传")
    private String fjsc;

}
