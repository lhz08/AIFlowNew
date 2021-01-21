package com.bdilab.aiflow.model.workflow;

import java.util.Map;

public class EpochInfo {
    private Integer epoch;
    private Double train_loss;
    private Double test_loss;
    private Double learning_rate;
    private String summary;
    private Boolean isEnd;
    private String basic_conclusion;
    Map<String,String> result;

    public Boolean getEnd() {
        return isEnd;
    }

    public Double getLearning_rate() {
        return learning_rate;
    }

    public Double getTest_loss() {
        return test_loss;
    }

    public Double getTrain_loss() {
        return train_loss;
    }

    public Integer getEpoch() {
        return epoch;
    }

    public String getBasic_conclusion() {
        return basic_conclusion;
    }

    public String getSummary() {
        return summary;
    }

    public void setEnd(Boolean end) {
        isEnd = end;
    }

    public void setEpoch(Integer epoch) {
        this.epoch = epoch;
    }

    public void setLearning_rate(Double learning_rate) {
        this.learning_rate = learning_rate;
    }

    public void setTest_loss(Double test_loss) {
        this.test_loss = test_loss;
    }

    public void setBasic_conclusion(String basic_conclusion) {
        this.basic_conclusion = basic_conclusion;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public void setTrain_loss(Double train_loss) {
        this.train_loss = train_loss;
    }

    public Map<String, String> getResult() {
        return result;
    }

    public void setResult(Map<String, String> result) {
        this.result = result;
    }

}
