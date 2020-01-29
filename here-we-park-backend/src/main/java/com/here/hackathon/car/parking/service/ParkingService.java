package com.here.hackathon.car.parking.service;

import com.here.hackathon.car.parking.model.BookingRequest;
import com.here.hackathon.car.parking.model.BookingResponse;
import com.here.hackathon.car.parking.model.ParkingSlot;
import com.here.hackathon.car.parking.model.ParkingSlots;
import com.here.hackathon.car.parking.model.Partition;
import com.here.hackathon.car.parking.model.Partitions;
import com.here.hackathon.car.parking.util.CPRestTemplate;
import com.here.hackathon.car.parking.util.Util;
import com.here.hrn.HRN;
import com.here.platform.data.client.spark.javadsl.JavaLayerDataFrameWriter;
import com.here.platform.data.client.spark.javadsl.VersionedDataConverter;
import com.here.platform.data.client.spark.scaladsl.GroupedData;
import com.here.platform.data.client.spark.scaladsl.VersionedRowMetadata;
import lombok.extern.slf4j.Slf4j;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;

@Service
@Slf4j
public class ParkingService implements Serializable {

    @Autowired
    private SparkSession sparkSession;

    @Autowired
    transient
    private CPRestTemplate cpRestTemplate;

    @Value("${olp.catalog.hrn}")
    private String olpCatalogHrn;

    @Value("${olp.catalog.layerId}")
    private String layerId;

    @Value("${olp.catalog.partition.handle.url}")
    private String partitionHandleUrl;

    @Value("${olp.catalog.partition.data.url}")
    private String partitionDataUrl;

    @Value("${olp.catalog.version.url}")
    private String catalogVersionUrl;

    private ParkingSlots parkings;

    @PostConstruct
    public void init() throws IOException {
        parkings = this.getParkingSlots();
    }

    public String insertParkingSlots(ParkingSlots parkingSlots){
        HRN catalogHrn = HRN.fromString(olpCatalogHrn);
        Dataset<Row> inputDataFrame = sparkSession.createDataFrame(parkingSlots.getParkingSlots(),
                ParkingSlot.class);

        JavaLayerDataFrameWriter.create(inputDataFrame)
                .writeLayer(catalogHrn, layerId)
                .withDataConverter(
                        new VersionedDataConverter() {
                            @Override
                            public GroupedData<VersionedRowMetadata> serializeGroup(
                                    VersionedRowMetadata rowMetadata, Iterator<Row> rows) {
                                return new GroupedData<>(rowMetadata, parkingSlots.getData());
                            }
                        })
                .save();

        sparkSession.stop();

        return "Success";
    }

    public ParkingSlots getParkingSlots() throws IOException {
        Partition catVersion = cpRestTemplate.getForEntity(String.format(catalogVersionUrl,olpCatalogHrn),Partition.class,null);
        String partitionHUrl  = String.format(partitionHandleUrl,olpCatalogHrn,layerId,catVersion.getVersion());
        Partitions partitions = cpRestTemplate.getForEntity(partitionHUrl, Partitions.class, null);
        if(partitions.getPartitions().stream().findFirst().isPresent()) {
            Partition partition = partitions.getPartitions().stream().findFirst().get();
            String pDataUrl = String.format(partitionDataUrl, olpCatalogHrn, layerId, partition.getDataHandle());
            String data = cpRestTemplate.getForEntity(pDataUrl, String.class, null);
            return Util.jsonToObject(data, ParkingSlots.class);
        }else return ParkingSlots.builder()
                .parkingSlots(new ArrayList<>())
                .build();
    }

    public BookingResponse bookParking(BookingRequest bookingRequest){
        ParkingSlot slot = parkings.getParkingSlots().stream()
                .filter(p -> !p.isBooked() &&
                        p.getBuildingId().equalsIgnoreCase(bookingRequest.getBuildingId()))
                .findAny()
                .orElseThrow(() -> new RuntimeException("No parking available in this building"));
        slot.setBooked(true);
        slot.setUserId(bookingRequest.getUserId());
        return BookingResponse.builder()
                .userId(bookingRequest.getUserId())
                .buildingId(slot.getBuildingId())
                .buildingName(slot.getBuildingName())
                .parkingId(slot.getParkingId())
                .floorId(slot.getFloorNumber())
                .build();
    }
}
