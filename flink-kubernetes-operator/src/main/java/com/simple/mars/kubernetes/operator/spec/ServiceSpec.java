package com.simple.mars.kubernetes.operator.spec;

import io.fabric8.kubernetes.api.model.Service;
import io.fabric8.kubernetes.api.model.ServiceBuilder;
import io.fabric8.kubernetes.api.model.ServicePortBuilder;

public class ServiceSpec {

    private Service service;

    public ServiceSpec(){
        this.service = new ServiceBuilder().withApiVersion("v1").withKind("Service").withNewMetadata().withName("flink-jobmanager").endMetadata()
                .withNewSpec().withType("ClusterIP")
                .addToPorts(
                        new ServicePortBuilder().withName("rpc").withPort(6123).build()
                )
                .addToPorts(
                        new ServicePortBuilder().withName("blob").withPort(6124).build()
                )
                .addToPorts(
                        new ServicePortBuilder().withName("ui").withPort(8081).build()
                )
                .addToSelector("app","flink")
                .addToSelector("component","jobmanager").endSpec().build();
    }

    public Service getService() {
        return this.service;
    }
}
