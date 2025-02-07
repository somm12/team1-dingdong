package com.ddbb.dingdong.domain.clustering.model;

import java.util.List;

public class Coordinate {
    public Double doubleValue;
    public List<Double> doubleArrayValue;

    public Coordinate(Double doubleValue) {
        this.doubleValue = doubleValue;
    }

    public Coordinate(List<Double> doubleArrayValue) {
        this.doubleArrayValue = doubleArrayValue;
    }
}
