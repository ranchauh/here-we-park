package com.here.hackathon.car.parking.controller;

import com.here.hackathon.car.parking.model.BookingRequest;
import com.here.hackathon.car.parking.model.BookingResponse;
import com.here.hackathon.car.parking.model.ParkingSlots;
import com.here.hackathon.car.parking.service.ParkingService;
import com.here.hackathon.car.parking.util.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
public class CarParkingController {

    @Autowired
    protected ParkingService parkingService;

    @PostMapping(value = "/parking",produces = MediaType.APPLICATION_JSON_VALUE)
    public String insertParkingSlots(@RequestBody ParkingSlots parkingSlots) throws IOException {

        parkingSlots = Util.loadClassPathResource("/parkings.json",ParkingSlots.class);

        return parkingService.insertParkingSlots(parkingSlots);
    }

    @GetMapping(value = "/parking",produces = MediaType.APPLICATION_JSON_VALUE)
    public ParkingSlots getParkingSlots() throws IOException {
        return parkingService.getParkingSlots();
    }

    @PostMapping(value = "/parking/book",produces = MediaType.APPLICATION_JSON_VALUE)
    public BookingResponse bookParkingSlot(@RequestBody BookingRequest bookingRequest) throws IOException {
        return parkingService.bookParking(bookingRequest);
    }
}
