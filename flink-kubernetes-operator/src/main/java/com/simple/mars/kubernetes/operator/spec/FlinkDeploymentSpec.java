package com.simple.mars.kubernetes.operator.spec;

import io.fabric8.kubernetes.api.model.*;
import io.fabric8.kubernetes.api.model.apps.Deployment;
import io.fabric8.kubernetes.api.model.apps.DeploymentBuilder;
import io.fabric8.kubernetes.api.model.apps.DeploymentSpec;
import io.fabric8.kubernetes.api.model.networking.v1beta1.*;
import io.fabric8.kubernetes.client.*;
import io.fabric8.kubernetes.client.Config;
import io.fabric8.kubernetes.client.dsl.PodResource;
import io.fabric8.kubernetes.client.dsl.Resource;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

@Component
@Data
public class FlinkDeploymentSpec {

    @Value("${flink.image}")
    private String image;

    @Value("${flink.imagePullPolicy}")
    private String imagePullPolicy;

    // TODO: 2023/2/14 to do if null
    @Value("#{${annotation}}")
    private Map<String,String> annotation;

    @Value("#{${label}}")
    private Map<String,String> label;

    @Value("#{${matchLabel}}")
    private Map<String,String> matchLabel;

    @Value("${flink.logback.xml}")
    private String logbackFile;

    @Value("${flink.configFile}")
    private String configFile;

    @Value("${flink.am.host}")
    private String host;
}

