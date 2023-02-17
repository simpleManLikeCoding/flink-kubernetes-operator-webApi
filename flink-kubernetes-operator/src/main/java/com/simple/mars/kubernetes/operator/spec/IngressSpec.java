package com.simple.mars.kubernetes.operator.spec;

import io.fabric8.kubernetes.api.model.IntOrString;

import io.fabric8.kubernetes.api.model.networking.v1beta1.*;
import lombok.Data;

@Data
public class IngressSpec {
    private String namespace;
    private Ingress ingress;
    private String host;

    public IngressSpec(String namespace, String host) {
        this.namespace = namespace;
        this.host = host;
        this.ingress = new IngressBuilder()
                .withNewMetadata().withName("flink-ingress")
                .addToAnnotations("kubernetes.io/ingress.class", "traefik").addToAnnotations("traefik.frontend.rule.type", "PathPrefixStrip")
                .endMetadata()
                .withNewSpec()
                .addToRules(
                        new IngressRuleBuilder().withHost(host).withNewHttp().addToPaths(
                                new HTTPIngressPathBuilder().withPath(String.format("/%s", namespace)).withBackend(
                                        new IngressBackendBuilder().withServiceName("flink-jobmanager").withServicePort(new IntOrString(8081)).build()
                                ).build()
                        ).endHttp().build()).endSpec().build();
    }
}
