package com.simple.mars.kubernetes.operator.spec;



import io.fabric8.kubernetes.api.model.*;
import io.fabric8.kubernetes.api.model.apps.Deployment;
import io.fabric8.kubernetes.api.model.apps.DeploymentBuilder;
import lombok.Data;


import java.util.*;

@Data
public class JobManagerDeploymentSpec {

    private FlinkDeploymentSpec flinkDeploymentSpec;

    private Deployment jobmanagerDeployment;

    private Map<String, String> annotation;

    private Map<String, String> label;

    private Map<String, String> matchLabel;

    private String image;

    private String imagePullPolicy;

    private String namespace;

    private Resource resource;

    private Map<String,Object> configmap;

    private String logback;

    public void setFlinkDeploymentSpecExtraLabel(Map<String, String> label) {
        this.label = new HashMap<>();
        this.label.putAll(this.flinkDeploymentSpec.getLabel());
        this.label.putAll(label);
    }

    public void setFlinkDeploymentSpecExtraAnnotation(Map<String, String> annotation) {
        this.annotation = new HashMap<>();
        this.annotation.putAll(this.flinkDeploymentSpec.getAnnotation());
        this.annotation.putAll(annotation);
    }

    public void setFlinkDeploymentSpecExtraMatchLabel(Map<String, String> matchLabel) {
        this.matchLabel = new HashMap<>();
        this.matchLabel.putAll(this.flinkDeploymentSpec.getMatchLabel());
        this.matchLabel.putAll(matchLabel);
    }

    public JobManagerDeploymentSpec(FlinkDeploymentSpec flinkDeploymentSpec,String namespace) {
        this.flinkDeploymentSpec = flinkDeploymentSpec;
        this.namespace = namespace;
    }


    public JobManagerDeploymentSpec buildJobmanagerDelpoyment() {
        Deployment tempDeployment = new DeploymentBuilder()
                .withApiVersion("apps/v1")
                .withKind("Deployment")
                .withNewMetadata()
                .withName("flink-jobmanager")
                .endMetadata()
                .withNewSpec()
                .withReplicas(1)
                .withNewSelector()
                .addToMatchLabels("app", "flink")
                .addToMatchLabels("component", "jobmanager")
                .addToMatchLabels(this.getMatchLabel())
                .endSelector()
                .withNewTemplate()
                .withNewMetadata()
                .addToLabels("component", "jobmanager")
                .addToLabels(this.label == null ? this.flinkDeploymentSpec.getLabel() : this.label)
                .addToAnnotations(this.annotation == null ? this.flinkDeploymentSpec.getAnnotation() : this.annotation)
                .endMetadata()
                .withNewSpec()
                .withTerminationGracePeriodSeconds(Long.valueOf("1"))
                .addNewContainer()
                .withName("jobmanager")
                .withImage(this.image == null ? this.flinkDeploymentSpec.getImage() : this.image)
                .withImagePullPolicy(this.imagePullPolicy == null ? this.flinkDeploymentSpec.getImagePullPolicy() : this.imagePullPolicy)
                .withArgs("jobmanager", "$(POD_IP)")
                .withWorkingDir("/opt/flink")
                .addNewEnv()
                .withName("POD_IP")
                .withNewValueFrom()
                .withNewFieldRef()
                .withFieldPath("status.podIP")
                .endFieldRef()
                .endValueFrom()
                .endEnv()
                .addToPorts(new ContainerPortBuilder().withName("rpc").withContainerPort(6123).build())
                .addToPorts(new ContainerPortBuilder().withName("blob").withContainerPort(6124).build())
                .addToPorts(new ContainerPortBuilder().withName("ui").withContainerPort(8081).build())
                .addToPorts(new ContainerPortBuilder().withName("ui-history").withContainerPort(8082).build())
                .addToPorts(new ContainerPortBuilder().withName("job-exporter").withContainerPort(9250).build())
                .withNewLivenessProbe()
                .withNewTcpSocket()
                .withPort(new IntOrString(6123))
                .endTcpSocket()
                .withInitialDelaySeconds(30)
                .withPeriodSeconds(30)
                .endLivenessProbe()
                .addToVolumeMounts(new VolumeMountBuilder().withName("flink-config-volume").withMountPath("/opt/flink/conf").build())
                .addToVolumeMounts(new VolumeMountBuilder().withName(String.format("%s-jobm-logdir-volume", getNamespace())).withMountPath("/opt/flink/log").build())
                .withNewSecurityContext()
                .withRunAsUser(9999L)
                .endSecurityContext()
                .withResources(this.resource == null ? null : this.resource.buildResourceRequirement().getResourceRequirements())
                .endContainer()
                .addToVolumes(new VolumeBuilder().withName(String.format("%s-jobm-logdir-volume", getNamespace())).withNewEmptyDir().endEmptyDir().build())
                .addToVolumes(new VolumeBuilder().withName("flink-config-volume").withNewConfigMap().withName("flink-config")
                        .addToItems(new KeyToPathBuilder().withKey("flink-conf.yaml").withPath("flink-conf.yaml").build())
                        .addToItems(new KeyToPathBuilder().withKey("logback-console.xml").withPath("logback-console.xml").build()).endConfigMap().build())
                .endSpec()
                .endTemplate()
                .endSpec()
                .build();

        this.jobmanagerDeployment = tempDeployment;
        return this;
    }
}
