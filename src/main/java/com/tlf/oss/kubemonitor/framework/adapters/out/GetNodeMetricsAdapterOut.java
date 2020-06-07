package com.tlf.oss.kubemonitor.framework.adapters.out;

import com.tlf.oss.kubemonitor.TopExample;
import com.tlf.oss.kubemonitor.application.queries.GetNodeMetricsQuery;
import com.tlf.oss.kubemonitor.domain.Node;
import io.fabric8.kubernetes.client.DefaultKubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClientException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class GetNodeMetricsAdapterOut implements GetNodeMetricsQuery {

    private static final Logger logger = LoggerFactory.getLogger(TopExample.class);
    List<Node> nodeList = new ArrayList<>();


    @Override
    public List<Node> getNodeMetrics() {
        try (KubernetesClient client = new DefaultKubernetesClient()) {

            client.nodes().list().getItems().forEach(k8sNode ->
            {
                Node node = new Node.Builder()
                        .withId(k8sNode.getStatus().getNodeInfo().getMachineID())
                        .withCpuCapacity(Double.valueOf(k8sNode.getStatus().getAllocatable().get("cpu").toString()) * 1000)
                        .withCpuUsage(Double.valueOf(client.top().nodes().metrics(k8sNode.getMetadata().getName()).getUsage().get("cpu").getAmount()))
                        .withCpuUsagePercentage()
                        .withMemoryCapacity(Long.valueOf(k8sNode.getStatus().getAllocatable().get("memory").toString().replace("Ki", "")))
                        .withMemoryUsage(Double.valueOf(client.top().nodes().metrics(k8sNode.getMetadata().getName()).getUsage().get("memory").getAmount()))
                        .withMemoryUsagePercentage()
                        .build();
                nodeList.add(node);
            });


        } catch (KubernetesClientException e) {
            logger.error(e.getMessage(), e);
        }

        return nodeList;
    }
}
