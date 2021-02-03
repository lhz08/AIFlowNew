package com.bdilab.aiflow.model.run;

public class ApiRunMetric {

    /*Required. The user defined name of the metric. It must between 1 and 63 characters long and must conform to the following regular expression: [a-z]([-a-z0-9]*[a-z0-9])?.*/
    private String name;

    /*Required. The runtime node ID which reports the metric. The node ID can be found in the RunDetail.workflow.Status. Metric with same (node_id, name) are considerd as duplicate. Only the first reporting will be recorded. Max length is 128.*/
    private String nodeId;

    /*number_value: number (double)*/
    /*The number value of the metric.*/
    private Number numberValue;

    /*
    * format: RunMetricFormat
    * RunMetricFormat: string , x ∈ { UNSPECIFIED (default) , RAW , PERCENTAGE }
    * The display format of metric.
    */
    private String format;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNodeId() {
        return nodeId;
    }

    public void setNodeId(String nodeId) {
        this.nodeId = nodeId;
    }

    public Number getNumberValue() {
        return numberValue;
    }

    public void setNumberValue(Number numberValue) {
        this.numberValue = numberValue;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }
}
