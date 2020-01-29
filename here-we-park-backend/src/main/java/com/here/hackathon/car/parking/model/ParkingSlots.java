package com.here.hackathon.car.parking.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.here.hackathon.car.parking.util.Util;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class ParkingSlots implements Serializable {

    private List<ParkingSlot> parkingSlots;

    @JsonIgnore
    private byte[] data;

    public static ParkingSlotsBuilder builder() {
        return new ParkingSlotsBuilder();
    }

    public byte[] getData(){
        try {
            return Util.toJson(this).getBytes();
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public static class ParkingSlotsBuilder {
        private List<ParkingSlot> parkingSlots;
        private byte[] data;

        ParkingSlotsBuilder() {
        }

        public ParkingSlotsBuilder parkingSlots(List<ParkingSlot> parkingSlots) {
            this.parkingSlots = parkingSlots;
            return this;
        }

        public ParkingSlotsBuilder data(byte[] data) {
            this.data = data;
            return this;
        }

        public ParkingSlots build() {
            ParkingSlots ps = new ParkingSlots();
            ps.setParkingSlots(this.parkingSlots);
            ps.setData(data);
            return ps;
        }

        public String toString() {
            return "ParkingSlots.ParkingSlotsBuilder(parkingSlots=" + this.parkingSlots + ", data=" + java.util.Arrays.toString(this.data) + ")";
        }
    }
}
