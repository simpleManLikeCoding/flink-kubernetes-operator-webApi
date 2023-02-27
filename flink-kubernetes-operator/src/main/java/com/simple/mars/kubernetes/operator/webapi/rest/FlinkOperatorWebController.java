package com.simple.mars.kubernetes.operator.webapi.rest;


import com.netease.mars.utilities.json.JsonUtils;
import com.simple.mars.kubernetes.operator.webapi.common.ServiceResponse;
import com.simple.mars.kubernetes.operator.webapi.service.IFlinkOperatorWebService;
import com.simple.mars.kubernetes.operator.webapi.webform.CreateFlinkClusterForm;
import com.simple.mars.kubernetes.operator.webapi.webform.StopFlinkClusterForm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/jflink")
public class FlinkOperatorWebController {

    @Autowired
    IFlinkOperatorWebService iFlinkOperatorWebService;

    private final Logger logger = LoggerFactory.getLogger(FlinkOperatorWebController.class);


    @PostMapping(value = "/start")
    public ServiceResponse start(@RequestBody CreateFlinkClusterForm createFlinkClusterForm) throws Exception {
        logger.info("incoming start request: " + JsonUtils.unsafeDumps(createFlinkClusterForm));
        return iFlinkOperatorWebService.createFlinkMiniCluster(createFlinkClusterForm);
    }

    @GetMapping(value = "/getPodListInNamespace")
    public ServiceResponse getPodListInNamespace(String namespace) throws Exception {
        logger.info("incoming getPodListInNamespace request: " + namespace);
        return iFlinkOperatorWebService.getPodListInNamespace(namespace);
    }

    @PostMapping(value = "/stopFLinkMiniCluster")
    public ServiceResponse stopFLinkMiniCluster(@RequestBody StopFlinkClusterForm stopFlinkClusterForm) throws Exception {
        logger.info("incoming getPodListInNamespace request: " + stopFlinkClusterForm);
        return iFlinkOperatorWebService.stopFLinkMiniCluster(stopFlinkClusterForm.getNamespace());
    }

    @GetMapping(value = "/judgeNameSpaceExist")
    public ServiceResponse judgeNameSpaceExist(String namespace) throws Exception {
        logger.info("incoming getPodListInNamespace request: " + namespace);
        return iFlinkOperatorWebService.judgeNameSpaceExist(namespace);
    }
}
