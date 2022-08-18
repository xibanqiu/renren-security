package io.renren.modules.act.mo.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 *
 * @Date 2020/9/15 9:36
 */
@Data
public class SoftwareTestProcQualifySealToOAReq implements Serializable {

    @ApiModelProperty(value = "标题")
    private String title;
    @ApiModelProperty(value = "申请人")
    private String sqr;
    @ApiModelProperty(value = "申请日期")
    private String sqrq;
    @ApiModelProperty(value = "印章证照所属公司")
    private String yzzzssgs;
    private String 	yzzzssgswb;
    @ApiModelProperty(value = "使用方式")
    private String syfs;
    @ApiModelProperty(value = "是否申请印章")
    private String sfsqyz;
    @ApiModelProperty(value = "印章类型")
    private String yzlx;
    @ApiModelProperty(value = "是否申请证照")
    private String sfsqzz;
    @ApiModelProperty(value = "证照类型")
    private String zzlx;
    @ApiModelProperty(value = "证照使用形式")
    private String zzsyxs;
    @ApiModelProperty(value = "文件是否关联原合同")
    private String wjsfglyht;
    @ApiModelProperty(value = "使用级别")
    private String syjb;
    @ApiModelProperty(value = "级别分类")
    private String jbfl;
    @ApiModelProperty(value = "文件是否为向政府机关、事业单位或监管机构提供的说明性文件")
    private String wjsfwzf;
    @ApiModelProperty(value = "文件接收机构")
    private String wjjsjg;
    @ApiModelProperty(value = "用途说明")
    private String ytsm;
    @ApiModelProperty(value = "附件")
    private String fjsc;
    @ApiModelProperty(value = "附件名称")
    private String fjscmc;
    @ApiModelProperty(value = "操作人")
    private String operatorId;
    @ApiModelProperty(value = "明细")
    private List<DetailInfo> detail;

    @Data
    public static class DetailInfo implements Serializable {
        private String wjmc;
        private String sl;

        public DetailInfo() {
        }

        public DetailInfo(String wjmc, String sl) {
            this.wjmc = wjmc;
            this.sl = sl;
        }
    }
}
