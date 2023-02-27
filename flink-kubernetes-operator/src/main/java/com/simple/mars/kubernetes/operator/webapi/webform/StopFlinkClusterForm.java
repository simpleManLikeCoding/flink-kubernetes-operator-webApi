package com.simple.mars.kubernetes.operator.webapi.webform;

import com.simple.mars.kubernetes.operator.spec.Resource;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.Map;

@Data
public class StopFlinkClusterForm {

    @ApiModelProperty(value = "mini cluster name", required = true, example = "jod1")
    @NotNull
    private String namespace;
}
