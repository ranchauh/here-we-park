package com.here.hackathon.car.parking.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class ParkingSlot implements Serializable {
    private String mt_partition;
    private String parkingId;
    private String buildingId;
    private String buildingName;
    private String floorNumber;
    private List<String> directions;
    @JsonIgnore
    private boolean booked;

    @JsonIgnore
    private String userId;
}
