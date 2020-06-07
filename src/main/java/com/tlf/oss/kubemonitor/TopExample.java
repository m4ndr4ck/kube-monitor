package com.tlf.oss.kubemonitor;

/**
 * Copyright (C) 2015 Red Hat, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import io.fabric8.kubernetes.api.model.metrics.v1beta1.NodeMetricsList;
import io.fabric8.kubernetes.client.DefaultKubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClientException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TopExample {

    private static final Logger logger = LoggerFactory.getLogger(TopExample.class);

    static double CPU;
    static double MEMORY;

    static double CPU_USAGE;
    static double MEMORY_USAGE;

    public static void main(String[] args) {
        try (KubernetesClient client = new DefaultKubernetesClient()) {
            NodeMetricsList nodeMetricList = client.top().nodes().metrics();


            client.nodes().list().getItems().forEach(node ->
                    {
                        System.out.println(node.getStatus().getAllocatable());
                    CPU = Long.valueOf(node.getStatus().getAllocatable().get("cpu").toString()) * 1000;
                    MEMORY = Long.valueOf(node.getStatus().getAllocatable().get("memory").toString().replace("Ki", ""));
                    });


            logger.info("==== Node Metrics  ====");
            nodeMetricList.getItems().forEach(nodeMetrics ->{
                CPU_USAGE = Double.valueOf(nodeMetrics.getUsage().get("cpu").getAmount());
                MEMORY_USAGE = Double.valueOf(nodeMetrics.getUsage().get("memory").getAmount());
                    logger.info("{}\tCPU: {}{}\tMemory: {}{}",
                            nodeMetrics.getMetadata().getName(),
                            nodeMetrics.getUsage().get("cpu").getAmount(), nodeMetrics.getUsage().get("cpu").getFormat(),
                            nodeMetrics.getUsage().get("memory").getAmount(), nodeMetrics.getUsage().get("memory").getFormat()
                    );});

            logger.info("==== Pod Metrics ====");
            client.top().pods().metrics("default").getItems().forEach(podMetrics ->
                    podMetrics.getContainers().forEach(containerMetrics ->
                            logger.info("{}\t{}\tCPU: {}{}\tMemory: {}{}",
                                    podMetrics.getMetadata().getName(), containerMetrics.getName(),
                                    containerMetrics.getUsage().get("cpu").getAmount(), containerMetrics.getUsage().get("cpu").getFormat(),
                                    containerMetrics.getUsage().get("memory").getAmount(), containerMetrics.getUsage().get("memory").getFormat()
                            ))
            );

            final String defaultNamespace = "kube-system";
            client.pods().inNamespace(defaultNamespace).list().getItems().stream().findFirst().map(pod -> {
                logger.info("==== Individual Pod Metrics ({}) ====", pod.getMetadata().getName());
                return client.top().pods().metrics(defaultNamespace, pod.getMetadata().getName());
            }).ifPresent(podMetrics ->
                    podMetrics.getContainers().forEach(containerMetrics ->
                            logger.info("{}\t{}\tCPU: {}{}\tMemory: {}{}",
                                    podMetrics.getMetadata().getName(), containerMetrics.getName(),
                                    containerMetrics.getUsage().get("cpu").getAmount(), containerMetrics.getUsage().get("cpu").getFormat(),
                                    containerMetrics.getUsage().get("memory").getAmount(), containerMetrics.getUsage().get("memory").getFormat()
                            ))
            );

        } catch (KubernetesClientException e) {
            logger.error(e.getMessage(), e);
        }

        System.out.println(CPU+" "+CPU_USAGE);

        System.out.println(MEMORY+" "+MEMORY_USAGE);

        System.out.println(CPU_USAGE * 100.0 / CPU + "%");

        System.out.println(MEMORY_USAGE * 100.0 / MEMORY + "%");

    }
}