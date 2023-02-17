package com.simple.mars.kubernetes.operator.spec;

import io.fabric8.kubernetes.api.model.Namespace;
import io.fabric8.kubernetes.api.model.NamespaceBuilder;
import lombok.Data;

@Data
public class NameSpaceSpec {
    private Namespace namespace;

    public NameSpaceSpec(String namespace){
        this.namespace = new NamespaceBuilder()
                .withApiVersion("v1")
                .withKind("Namespace")
                .withNewMetadata().withName(namespace).addToLabels("name", namespace).addToLabels("app", "flink").endMetadata().build();
    }
}
