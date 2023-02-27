package com.simple.mars.kubernetes.operator.client;

import com.simple.mars.kubernetes.operator.pod.PodInfo;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.PodList;
import io.fabric8.kubernetes.api.model.PodStatus;
import io.fabric8.kubernetes.client.Config;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.impl.KubernetesClientImpl;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Component
public class ClientResource {

    @Value("${k8s.configFile}")
    private String configFile;

    private KubernetesClient kubernetesClient;

    public KubernetesClient getKubernetesClient() throws IOException {
        if (this.kubernetesClient != null){
            return this.kubernetesClient;
        }else {
            String yaml = String.join("\n", Files.readAllLines(Paths.get(configFile)));
            Config config = Config.fromKubeconfig(yaml);
            this.kubernetesClient = new KubernetesClientImpl(config);
            return this.kubernetesClient;
        }
    }

    public void closeKubernetesClient() throws IOException {
        this.kubernetesClient.close();
    }
}
