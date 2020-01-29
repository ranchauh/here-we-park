package com.here.hackathon.car.parking.model;

import lombok.Data;

@Data
public class BookingResponse {
    private String userId;
    private String buildingId;
    private String buildingName;
    private String floorId;
    private String parkingId;

    public static BookingResponseBuilder builder() {
        return new BookingResponseBuilder();
    }

    public static class BookingResponseBuilder {
        private String userId;
        private String buildingId;
        private String buildingName;
        private String floorId;
        private String parkingId;

        BookingResponseBuilder() {
        }

        public BookingResponseBuilder userId(String userId) {
            this.userId = userId;
            return this;
        }

        public BookingResponseBuilder buildingId(String buildingId) {
            this.buildingId = buildingId;
            return this;
        }

        public BookingResponseBuilder buildingName(String buildingName) {
            this.buildingName = buildingName;
            return this;
        }

        public BookingResponseBuilder floorId(String floorId) {
            this.floorId = floorId;
            return this;
        }

        public BookingResponseBuilder parkingId(String parkingId) {
            this.parkingId = parkingId;
            return this;
        }

        public BookingResponse build() {
            BookingResponse bookingResponse  = new BookingResponse();
            bookingResponse.setUserId(userId);
            bookingResponse.setBuildingId(buildingId);
            bookingResponse.setBuildingName(buildingName);
            bookingResponse.setFloorId(floorId);
            bookingResponse.setParkingId(parkingId);
            return bookingResponse;
        }

        public String toString() {
            return "BookingResponse.BookingResponseBuilder(userId=" + this.userId + ", buildingId=" + this.buildingId + ", buildingName=" + this.buildingName + ", floorId=" + this.floorId + ", parkingId=" + this.parkingId + ")";
        }
    }
}
