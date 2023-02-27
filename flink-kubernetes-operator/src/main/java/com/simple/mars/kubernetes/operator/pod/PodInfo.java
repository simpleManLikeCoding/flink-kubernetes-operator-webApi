package com.simple.mars.kubernetes.operator.pod;


import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.netease.mars.utilities.json.JsonUtils;
import com.netease.mars.utilities.network.HttpUtils;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.PodList;
import io.fabric8.kubernetes.client.KubernetesClient;
import lombok.Data;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class PodInfo {
    private String namespace;
    private String podName;
    private String podStatus;
    private String podRunningTime;
    private String podFailedReason;
    private String virtualIp;
    private String hostIp;
    private String jmHost;

    public PodInfo(Pod pod, String jmHost) {
        this.namespace = pod.getMetadata().getNamespace();
        this.podName = pod.getMetadata().getName();
        this.podStatus = pod.getStatus().getPhase();
        this.podRunningTime = pod.getStatus().getStartTime();
        this.podFailedReason = pod.getStatus().getReason();
        this.virtualIp = pod.getStatus().getPodIP();
        this.hostIp = pod.getStatus().getHostIP();
        this.jmHost = jmHost;
    }

    public PodInfo(Pod pod) {
        this.namespace = pod.getMetadata().getNamespace();
        this.podName = pod.getMetadata().getName();
        this.podStatus = pod.getStatus().getPhase();
        this.podRunningTime = pod.getStatus().getStartTime();
        this.podFailedReason = pod.getStatus().getReason();
        this.virtualIp = pod.getStatus().getPodIP();
        this.hostIp = pod.getStatus().getHostIP();
        this.jmHost = jmHost;
    }

    public boolean holdToConnect() throws InterruptedException {
        int count = 0;
        while (count < 60) {
            try {
                HttpUtils.Builder builder = new HttpUtils.Builder();
                HttpUtils.HttpResponse httpResponse = builder.get(String.format("%s/overview",this.jmHost), new HashMap<>());
                if (httpResponse.code == 200) {
                    Map<String, Object> objectMapper = JsonUtils.unsafeParse(httpResponse.content);
                    if (objectMapper.containsKey("taskmanagers")) {
                        return true;
                    } else {
                        Thread.sleep(1000);
                        count++;
                    }
                } else {
                    Thread.sleep(1000);
                    count++;
                }
            } catch (IOException | URISyntaxException e) {
                Thread.sleep(1000);
                count++;
            }
        }
        return false;
    }

    public static List<PodInfo> getPodInfo(KubernetesClient kubernetesClient, String namespace) {
        List<PodInfo> podInfos = new ArrayList<>();
        PodList podList = kubernetesClient.pods().inNamespace(namespace).withLabel("component", "jobmanager").list();
        PodInfo podInfo = new PodInfo(podList.getItems().get(0));
        podInfos.add(podInfo);
        podList = kubernetesClient.pods().inNamespace(namespace).withLabel("component", "taskmanager").list();
        for (Pod pod : podList.getItems()) {
            podInfo = new PodInfo(pod);
            podInfos.add(podInfo);
        }
        return podInfos;
    }
}
