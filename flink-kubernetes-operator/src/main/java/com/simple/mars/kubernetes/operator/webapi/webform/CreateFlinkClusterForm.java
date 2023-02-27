package com.simple.mars.kubernetes.operator.webapi.webform;

import com.simple.mars.kubernetes.operator.spec.Resource;
import lombok.Data;

import javax.validation.constraints.NotNull;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.Map;

@Data
public class CreateFlinkClusterForm {

    @ApiModelProperty(value = "mini cluster name", required = true, example = "jod1")
    @NotNull
    private String namespace;

    @ApiModelProperty(value = "flink conf", required = true, example = "{\"state.backend\": \"rocksdb\"}")
    @NotNull
    private Map<String,Object> flinkConf;

    private String image;

    private String imagePullPolicy;

    private Resource jobmanagerResource;

    private Resource taskmanagerResource;

    @ApiModelProperty(value = "flink conf", required = true, example = "{\"state.backend\": \"rocksdb\"}")
    @NotNull
    private Integer replicas;
}
