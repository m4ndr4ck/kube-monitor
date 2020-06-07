package com.tlf.oss.kubemonitor;

import com.tlf.oss.kubemonitor.framework.adapters.out.CreateNodeAdapterOut;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
public class KubeMonitorApplication {

    public static void main(String[] args) throws Exception {
        SpringApplication.run(KubeMonitorApplication.class, args);
        new CreateNodeAdapterOut().createNode();
    }

}
