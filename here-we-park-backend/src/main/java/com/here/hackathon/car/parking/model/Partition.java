package com.here.hackathon.car.parking.model;

import lombok.Data;

@Data
public class Partition {
    private String version;
    private String layer;
    private String partition;
    private String dataHandle;
}
