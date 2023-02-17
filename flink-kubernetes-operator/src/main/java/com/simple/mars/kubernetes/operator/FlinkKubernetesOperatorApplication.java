package com.simple.mars.kubernetes.operator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})

public class FlinkKubernetesOperatorApplication {

    public static void main(String[] args) {
        SpringApplication.run(FlinkKubernetesOperatorApplication.class, args);
    }

}
