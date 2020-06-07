package com.tlf.oss.kubemonitor.framework.adapters.out;

import com.tlf.oss.kubemonitor.application.ports.out.CreateNodePortOut;
import com.tlf.oss.kubemonitor.domain.Node;

public class CreateNodeAdapterOut implements CreateNodePortOut {

    @Override
    public void createNode() {
        try{
            Node.checkNodeMetrics();
        }catch (Exception e){
            e.printStackTrace();
        }

    }

}
