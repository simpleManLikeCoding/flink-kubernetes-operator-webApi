package com.simple.mars.kubernetes.operator.spec;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.fabric8.kubernetes.api.model.ConfigMap;
import io.fabric8.kubernetes.api.model.ConfigMapBuilder;
import lombok.Data;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;


@Data
public class ConfigMapSpec {
    private FlinkDeploymentSpec flinkDeploymentSpec;

    private Map<String, Object> loadConfig;

    private String logback;

    private ConfigMap configMap;

    public ConfigMapSpec(FlinkDeploymentSpec flinkDeploymentSpec) throws IOException {
        this.flinkDeploymentSpec = flinkDeploymentSpec;
        Yaml yaml = new Yaml();
        InputStream f = new FileInputStream(this.flinkDeploymentSpec.getConfigFile());
        this.loadConfig = (Map<String, Object>) yaml.load(f);
        this.logback = String.join("\n", Files.readAllLines(Paths.get(flinkDeploymentSpec.getLogbackFile())));
    }

    public void setFlinkDeploymentSpecExtraFlinkConf(Map<String, Object> flinkConf) {
        this.loadConfig.putAll(flinkConf);
    }

    public ConfigMap buildConfigMap() throws JsonProcessingException {
        DumperOptions options = new DumperOptions();
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        options.setPrettyFlow(true);
        Yaml yaml = new Yaml(options);
        String output = yaml.dump(loadConfig);
        this.configMap = new ConfigMapBuilder()
                .withApiVersion("v1")
                .withKind("ConfigMap")
                .withNewMetadata().withName("flink-config").addToLabels("app", "flink").endMetadata()
                .addToData("flink-conf.yaml", output)
                .addToData("logback-console.xml",logback).build();
        return this.configMap;
    }

}
