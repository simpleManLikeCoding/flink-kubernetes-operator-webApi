package com.simple.mars.kubernetes.operator.spec;

import io.fabric8.kubernetes.api.model.Quantity;
import io.fabric8.kubernetes.api.model.ResourceRequirements;
import io.fabric8.kubernetes.api.model.ResourceRequirementsBuilder;
import lombok.Data;

import java.util.Collections;

@Data
public class Resource {
    /**
     * Amount of CPU allocated to the pod. Example: 0.1 , 1
     */
    private Double requestCpu;

    /**
     * Amount of memory allocated to the pod. Example: 1024m, 1g
     */
    private String requestMemory;

    /**
     * Amount of CPU limit to the pod. Example: 0.1 , 1
     */
    private Double limitCpu;

    /**
     * Amount of memory limit to the pod. Example: 1024m, 1g
     */
    private String limitMemory;

    private ResourceRequirements resourceRequirements;

    private Resource(Double requestCpu, String requestMemory) {
        this.requestCpu = requestCpu;
        this.requestMemory = requestMemory;
        this.resourceRequirements = new ResourceRequirementsBuilder()
                .addToRequests(Collections.singletonMap("cpu", new Quantity(requestCpu.toString())))//做成可选择
                .addToRequests(Collections.singletonMap("memory", new Quantity(requestMemory))).build();
    }

    private Resource(Double requestCpu, String requestMemory, Double limitCpu, String limitMemory) {
        this.requestCpu = requestCpu;
        this.requestMemory = requestMemory;
        this.limitCpu = limitCpu;
        this.limitMemory = limitMemory;
        this.resourceRequirements = new ResourceRequirementsBuilder()
                .addToRequests(Collections.singletonMap("cpu", new Quantity(requestCpu.toString())))//做成可选择
                .addToRequests(Collections.singletonMap("memory", new Quantity(requestMemory)))
                .addToLimits(Collections.singletonMap("cpu", new Quantity(limitCpu.toString())))//做成可选择
                .addToLimits(Collections.singletonMap("memory", new Quantity(limitMemory))).build();
    }

    public static Resource Of(Double requestCpu, String requestMemory) {
        return new Resource(requestCpu, requestMemory);
    }

    public static Resource Of(Double requestCpu, String requestMemory, Double limitCpu, String limitMemory) {
        return new Resource(requestCpu, requestMemory, limitCpu, limitMemory);
    }



}
