package io.renren.common.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.util.StringUtils;

import java.io.Serializable;

/**
 * @ClassName PageQueryInfo
 * @Description TODO
 * @Author weizhao.yao
 * @Date 2020/3/10 13:43
 */
@ApiModel
@Data()
public class PageQueryInfo implements Serializable {
    @ApiModelProperty(value = "第几页")
    private Integer page = 1;
    @ApiModelProperty(value = "每页大小")
    private Integer limit = 20;
    @ApiModelProperty(value = "排序名称", hidden = true)
    private String sidx;
    @ApiModelProperty(value = "排序方式", hidden = true)
    private String order;
    /**
     * 设置默认排序方式
     */
    public void setDefaultOrder(String orderName, String orderDesc) {
        if (StringUtils.isEmpty(this.sidx) || StringUtils.isEmpty(this.order)) {
            setSidx(orderName);
            setOrder(orderDesc);
        }
    }
}
