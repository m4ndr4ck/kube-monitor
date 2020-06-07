package com.tlf.oss.kubemonitor.application.queries;

import com.tlf.oss.kubemonitor.domain.Node;

import java.util.List;

public interface GetNodeMetricsQuery {

    List<Node> getNodeMetrics();
}
