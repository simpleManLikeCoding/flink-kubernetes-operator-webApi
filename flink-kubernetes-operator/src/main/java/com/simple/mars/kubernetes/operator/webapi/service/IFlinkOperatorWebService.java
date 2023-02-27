package com.simple.mars.kubernetes.operator.webapi.service;

import com.simple.mars.kubernetes.operator.webapi.common.ServiceResponse;
import com.simple.mars.kubernetes.operator.webapi.webform.CreateFlinkClusterForm;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URISyntaxException;

@Service
public interface IFlinkOperatorWebService {
    ServiceResponse createFlinkMiniCluster(CreateFlinkClusterForm createFlinkClusterForm) throws IOException, URISyntaxException, InterruptedException;

    ServiceResponse stopFLinkMiniCluster(String namespace) throws IOException, InterruptedException;

    ServiceResponse getPodListInNamespace(String namespace) throws IOException;

    ServiceResponse judgeNameSpaceExist(String namespace) throws IOException;
}
