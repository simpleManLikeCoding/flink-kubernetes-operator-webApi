package com.simple.mars.kubernetes.operator.spec;

import io.fabric8.kubernetes.api.model.Quantity;
import io.fabric8.kubernetes.api.model.ResourceRequirements;
import io.fabric8.kubernetes.api.model.ResourceRequirementsBuilder;
import lombok.Data;

import java.util.Collections;
import java.util.Optional;

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

    public Resource() {
    }

    public Resource buildResourceRequirement() {
        ResourceRequirementsBuilder resourceRequirementsBuilder = new ResourceRequirementsBuilder();
        Optional.ofNullable(this.requestMemory).ifPresent(t -> resourceRequirementsBuilder.addToRequests(Collections.singletonMap("memory", new Quantity(t))));
        Optional.ofNullable(this.requestCpu).ifPresent(t -> resourceRequirementsBuilder.addToRequests(Collections.singletonMap("cpu", new Quantity(t.toString()))));
        Optional.ofNullable(this.limitMemory).ifPresent(t -> resourceRequirementsBuilder.addToLimits(Collections.singletonMap("memory", new Quantity(t))));
        Optional.ofNullable(this.limitCpu).ifPresent(t -> resourceRequirementsBuilder.addToLimits(Collections.singletonMap("cpu", new Quantity(t.toString()))));
        this.resourceRequirements = resourceRequirementsBuilder.build();
        return this;
    }


}
