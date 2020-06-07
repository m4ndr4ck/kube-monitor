package com.tlf.oss.kubemonitor.domain;

import com.tlf.oss.kubemonitor.framework.adapters.out.GetNodeMetricsAdapterOut;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * O objeto {@link Node} representa um nó do Kubernetes
 */
public class Node {

    @Getter
    @Setter
    private String id;

    @Getter
    @Setter
    private double cpuCapacity;
    @Getter
    @Setter
    private double cpuUsage;
    @Getter
    @Setter
    private double cpuUsagePercentage;

    @Getter
    @Setter
    private double memoryCapacity;
    @Getter
    @Setter
    private double memoryUsage;
    @Getter
    @Setter
    private double memoryUsagePercentage;

    public static class Builder {

    String id;

    double cpuCapacity;
    double cpuUsage;
    double cpuUsagePercentage;

    double memoryCapacity;
    double memoryUsage;
    double memoryUsagePercentage;


        public Builder withId(String id){
            this.id = id;
            return this;
        }

        public Builder withCpuCapacity(double cpuCapacity){
            this.cpuCapacity = cpuCapacity;
            return this;
        }

        public Builder withCpuUsage(double cpuUsage){
            this.cpuUsage = cpuUsage;
            return this;
        }

        public Builder withCpuUsagePercentage(){
            this.cpuUsagePercentage = getPercentage(this.cpuUsage, this.cpuCapacity);
            return this;
        }

        public Builder withMemoryCapacity(double memoryCapacity){
            this.memoryCapacity = memoryCapacity;
            return this;
        }

        public Builder withMemoryUsage(double memoryUsage){
            this.memoryUsage = memoryUsage;
            return this;
        }

        public Builder withMemoryUsagePercentage(){
            this.memoryUsagePercentage = getPercentage(this.memoryUsage, this.memoryCapacity);
            return this;
        }

        public Node build(){
            Node node = new Node();
            node.id = this.id;
            node.cpuCapacity = this.cpuCapacity;
            node.cpuUsage = this.cpuUsage;
            node.cpuUsagePercentage = this.cpuUsagePercentage;
            node.memoryCapacity = this.memoryCapacity;
            node.memoryUsage = this.memoryUsage;
            node.memoryUsagePercentage = this.memoryUsagePercentage;
            return node;
        }

    }

    static double getPercentage(double usage, double capacity){
        return usage * 100.0 / capacity;
    }


    public static List<Node> getNodeMetrics() throws  Exception {
        List<Node> nodeMetrics = new GetNodeMetricsAdapterOut().getNodeMetrics();
        return nodeMetrics;
    }

    public static void checkNodeMetrics() throws  Exception{
        int count = 0;
        ScheduledExecutorService ses = Executors.newScheduledThreadPool(1);
        Runnable task1 = () -> {
            try {
                getNodeMetrics().forEach(x -> System.out.println(x));
            } catch (Exception e){
                e.printStackTrace();
            }
        };

        ScheduledFuture<?> scheduledFuture = ses.scheduleAtFixedRate(task1, 5, 1, TimeUnit.SECONDS);


        while (true) {
            Thread.sleep(1000);
            if (count == 5) {
                System.out.println("Count is 5, cancel the scheduledFuture!");
                scheduledFuture.cancel(true);
                ses.shutdown();
                break;
            }
        }

        ses.shutdown();
    }

    @Override
    public String toString(){
        return "CPU Capacity: "+this.cpuCapacity+" | "+
                "CPU Usage: "+this.cpuUsagePercentage+" | "+
                "Memory Capacity: "+this.memoryCapacity+" | "+
                "Memory Usage: "+this.memoryUsagePercentage;
    }



}
