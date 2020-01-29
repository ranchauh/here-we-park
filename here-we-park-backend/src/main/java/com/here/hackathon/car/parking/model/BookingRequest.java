package com.here.hackathon.car.parking.model;

import lombok.Data;

@Data
public class BookingRequest {
    private String userId;
    private String buildingId;
    private String buildingName;
}
