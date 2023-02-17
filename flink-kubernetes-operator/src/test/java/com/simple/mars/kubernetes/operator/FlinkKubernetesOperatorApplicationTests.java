package com.simple.mars.kubernetes.operator;

import com.simple.mars.kubernetes.operator.client.ClientResource;
import com.simple.mars.kubernetes.operator.spec.*;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.dsl.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@SpringBootTest(classes = FlinkKubernetesOperatorApplication.class)
class FlinkKubernetesOperatorApplicationTests {

    @Autowired
    private FlinkDeploymentSpec flinkDeploymentSpec;

    @Autowired
    private ClientResource clientResource;

    @Test
    void contextLoads() {
    }

    @Test
    public void initFlinkNamespace() throws IOException {
        String namespace = "temp-namespace";
        KubernetesClient client = clientResource.getKubernetesClient();
        NameSpaceSpec nameSpaceSpec = new NameSpaceSpec(namespace);
        Resource namespaceResource = client.namespaces().resource(nameSpaceSpec.getNamespace());
        namespaceResource.create();
    }

    @Test
    public void initConfigMap() throws IOException {
        String namespace = "temp-namespace";
        Map<String, Object> flinkConf = new HashMap<>();
        flinkConf.put("taskmanager.numberOfTaskSlots", 1);
        flinkConf.put("jobmanager.memory.process.size", "2048m");
        flinkConf.put("taskmanager.memory.process.size", "2048m");
        ConfigMapSpec configMapSpec = new ConfigMapSpec(flinkDeploymentSpec);
        configMapSpec.setFlinkDeploymentSpecExtraFlinkConf(flinkConf);
        KubernetesClient client = clientResource.getKubernetesClient();
        Resource configmap = client.configMaps().inNamespace(namespace).resource(configMapSpec.buildConfigMap());
        configmap.create();
        configmap.waitUntilReady(20, TimeUnit.SECONDS);
    }

    @Test
    public void initFlinkDeploymentSpec() throws IOException {
        String namespace = "temp-namespace";
        KubernetesClient client = clientResource.getKubernetesClient();
        JobManagerDeploymentSpec jobManagerDeploymentSpec = new JobManagerDeploymentSpec(flinkDeploymentSpec, namespace);
        jobManagerDeploymentSpec.setResource(com.simple.mars.kubernetes.operator.spec.Resource.Of(0.1, "2048m", 1.0, "12048m"));
        Resource jmResource = client.apps().deployments().inNamespace(namespace).resource(jobManagerDeploymentSpec.buildJobmanagerDelpoyment().getJobmanagerDeployment());
        jmResource.create();
        jmResource.waitUntilReady(20, TimeUnit.SECONDS);
    }

    @Test
    public void initFlinkServiceSpec() throws IOException {
        String namespace = "temp-namespace";
        KubernetesClient client = clientResource.getKubernetesClient();
        Resource service = client.services().inNamespace(namespace).resource(new ServiceSpec().getService());
        service.create();
        service.waitUntilReady(20, TimeUnit.SECONDS);
    }

    @Test
    public void initFlinkIngressSpec() throws IOException {
        String namespace = "temp-namespace";
        KubernetesClient client = clientResource.getKubernetesClient();
        Resource ingress = client.network().ingress().inNamespace(namespace).resource(new IngressSpec(namespace,this.flinkDeploymentSpec.getHost()).getIngress());
        ingress.create();
        ingress.waitUntilReady(20,TimeUnit.SECONDS);
    }

    @Test
    public void initFlinkTaskmanagerSpec() throws IOException {
        String namespace = "temp-namespace";
        KubernetesClient client = clientResource.getKubernetesClient();
        TaskManagerDeploymentSpec taskManagerDeploymentSpec = new TaskManagerDeploymentSpec(flinkDeploymentSpec, namespace,5);
        taskManagerDeploymentSpec.setResource(com.simple.mars.kubernetes.operator.spec.Resource.Of(0.1, "2048m", 1.0, "12048m"));
        Resource jmResource = client.apps().deployments().inNamespace(namespace).resource(taskManagerDeploymentSpec.buildTaskManagerDeployment().getTaskmanagerDeloyment());
        jmResource.create();
        jmResource.waitUntilReady(20, TimeUnit.SECONDS);
    }

    @Test
    public void initStopFlink() throws IOException {
        String namespace = "temp-namespace";
        KubernetesClient client = clientResource.getKubernetesClient();
        client.namespaces().withName(namespace).delete();
    }

}
