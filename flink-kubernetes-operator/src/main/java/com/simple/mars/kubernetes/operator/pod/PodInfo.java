package com.simple.mars.kubernetes.operator.pod;


import lombok.Data;

@Data
public class PodInfo {
    private String namespace;
    private String podName;
    private String podStatus;
    private String podRunningTime;
    private String podFailedReason;


    public PodInfo(String podName){
        this.namespace = namespace;
    }
}
