package com.simple.mars.kubernetes.operator.webapi.service;

import com.netease.mars.utilities.json.JsonUtils;
import com.simple.mars.kubernetes.operator.client.ClientResource;
import com.simple.mars.kubernetes.operator.pod.PodInfo;
import com.simple.mars.kubernetes.operator.spec.*;
import com.simple.mars.kubernetes.operator.webapi.common.ServiceResponse;
import com.simple.mars.kubernetes.operator.webapi.webform.CreateFlinkClusterForm;
import io.fabric8.kubernetes.api.model.PodList;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.LocalPortForward;
import io.fabric8.kubernetes.client.dsl.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Component
public class FlinkOperatorWebServiceImpl implements IFlinkOperatorWebService {
    @Autowired
    private FlinkDeploymentSpec flinkDeploymentSpec;

    private Map<String, LocalPortForward> port = new HashMap<>();

    @Autowired
    private ClientResource clientResource;

    private static final Logger logger = LoggerFactory.getLogger(FlinkOperatorWebServiceImpl.class);

    @Override
    public ServiceResponse createFlinkMiniCluster(CreateFlinkClusterForm createFlinkClusterForm) throws IOException, URISyntaxException, InterruptedException {
        String namespace = createFlinkClusterForm.getNamespace();
        KubernetesClient client = clientResource.getKubernetesClient();
        try {
            //create namespace
            NameSpaceSpec nameSpaceSpec = new NameSpaceSpec(namespace);
            Resource namespaceResource = client.namespaces().resource(nameSpaceSpec.getNamespace());
            if (namespaceResource.isReady()) {
                return ServiceResponse.createBySuccess("this namespace still exist", 200);
            }
            namespaceResource.create();

            //create configmap
            ConfigMapSpec configMapSpec = new ConfigMapSpec(flinkDeploymentSpec);
            configMapSpec.setFlinkDeploymentSpecExtraFlinkConf(createFlinkClusterForm.getFlinkConf());
            Resource configmap = client.configMaps().inNamespace(namespace).resource(configMapSpec.buildConfigMap());
            configmap.create();

            //create jobmanager
            JobManagerDeploymentSpec jobManagerDeploymentSpec = new JobManagerDeploymentSpec(flinkDeploymentSpec, namespace);
            Optional.ofNullable(createFlinkClusterForm.getImage()).ifPresent(t -> jobManagerDeploymentSpec.setImage(t));
            Optional.ofNullable(createFlinkClusterForm.getImagePullPolicy()).ifPresent(t -> jobManagerDeploymentSpec.setImagePullPolicy(t));
            Optional.ofNullable(createFlinkClusterForm.getJobmanagerResource()).ifPresent(t -> jobManagerDeploymentSpec.setResource(t));

            Resource jmResource = client.apps().deployments().inNamespace(namespace).resource(jobManagerDeploymentSpec.buildJobmanagerDelpoyment().getJobmanagerDeployment());
            jmResource.create();

            //create service,ingress
            Resource service = client.services().inNamespace(namespace).resource(new ServiceSpec().getService());
            service.create();


            //create taskmanager
            TaskManagerDeploymentSpec taskManagerDeploymentSpec = new TaskManagerDeploymentSpec(flinkDeploymentSpec, namespace, createFlinkClusterForm.getReplicas());
            Optional.ofNullable(createFlinkClusterForm.getImage()).ifPresent(t -> taskManagerDeploymentSpec.setImage(t));
            Optional.ofNullable(createFlinkClusterForm.getImagePullPolicy()).ifPresent(t -> taskManagerDeploymentSpec.setImagePullPolicy(t));
            Optional.ofNullable(createFlinkClusterForm.getTaskmanagerResource()).ifPresent(t -> taskManagerDeploymentSpec.setResource(t));

            Resource tmResource = client.apps().deployments().inNamespace(namespace).resource(taskManagerDeploymentSpec.buildTaskManagerDeployment().getTaskmanagerDeloyment());
            tmResource.create();
            tmResource.waitUntilReady(20, TimeUnit.SECONDS);

            //jobmanager Running
            PodList podList = client.pods().inNamespace(namespace).withLabel("component", "jobmanager").list();
            String podName = podList.getItems().get(0).getMetadata().getName();
            String jobmanagerUrl;
            if (this.flinkDeploymentSpec.getJmHost() == null){
                LocalPortForward portForward = client.pods().inNamespace(namespace).withName(podName).portForward(8081);
                port.put(namespace, portForward);
                jobmanagerUrl = String.format("http://127.0.0.1:%s", portForward.getLocalPort());
                /*
                if use LocalportForward,when the program stops,existing "portForward" will be lost
                 */
                logger.info(String.format("Port forwarded at http://127.0.0.1:%s", portForward.getLocalPort()));
            }else {
                Resource ingress = client.network().ingress().inNamespace(namespace).resource(new IngressSpec(namespace, this.flinkDeploymentSpec.getJmHost()).getIngress());
                ingress.create();
                jobmanagerUrl = String.format("http://%s/%s", this.flinkDeploymentSpec.getJmHost(), namespace);
            }
            PodInfo podInfo = new PodInfo(podList.getItems().get(0),jobmanagerUrl);
            if (podInfo.holdToConnect()) {
                Map data = new HashMap();
                data.put("url", jobmanagerUrl);
                return ServiceResponse.createBySuccess("OK", 0,data);
            } else {
                stopFLinkMiniCluster(createFlinkClusterForm.getNamespace());
                return ServiceResponse.createByError("k8s init flink too long", 514);
            }
        } catch (Exception e) {
            stopFLinkMiniCluster(createFlinkClusterForm.getNamespace());
            throw e;
        }
    }

    @Override
    public ServiceResponse stopFLinkMiniCluster(String namespace) throws IOException, InterruptedException {
        KubernetesClient client = clientResource.getKubernetesClient();
        client.namespaces().withName(namespace).delete();
        if (port.containsKey(namespace)) {
            LocalPortForward portForward = port.get(namespace);
            portForward.close();
        }
        while (true) {
            if (client.namespaces().withName(namespace).get() == null) {
                return ServiceResponse.createBySuccess("OK", 0);
            } else {
                Thread.sleep(1000);
            }
        }
    }

    @Override
    public ServiceResponse getPodListInNamespace(String namespace) throws IOException {
        KubernetesClient client = clientResource.getKubernetesClient();
        NameSpaceSpec nameSpaceSpec = new NameSpaceSpec(namespace);
        Resource namespaceResource = client.namespaces().resource(nameSpaceSpec.getNamespace());
        if (!namespaceResource.isReady()) {
            return ServiceResponse.createBySuccess("this namespace is not exist", 201);
        }
        return ServiceResponse.createBySuccess("OK", 0, PodInfo.getPodInfo(client, namespace));
    }

    @Override
    public ServiceResponse judgeNameSpaceExist(String namespace) throws IOException {
        KubernetesClient client = clientResource.getKubernetesClient();
        NameSpaceSpec nameSpaceSpec = new NameSpaceSpec(namespace);
        Resource namespaceResource = client.namespaces().resource(nameSpaceSpec.getNamespace());
        if (namespaceResource.isReady()) {
            return ServiceResponse.createByError("this namespace still exist", 200);
        } else {
            return ServiceResponse.createBySuccess("OK", 0);
        }

    }
}
