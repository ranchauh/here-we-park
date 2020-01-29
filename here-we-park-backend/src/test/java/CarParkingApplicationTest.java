import akka.actor.ActorSystem;
import akka.actor.CoordinatedShutdown;
import com.here.hackathon.car.parking.model.ParkingSlot;
import com.here.hrn.HRN;
import com.here.platform.data.client.javadsl.AdminApi;
import com.here.platform.data.client.javadsl.DataClient;
import com.here.platform.data.client.spark.javadsl.JavaLayerDataFrameReader;
import com.here.platform.data.client.spark.javadsl.JavaLayerDataFrameWriter;
import com.here.platform.data.client.spark.javadsl.JavaLayerUpdater;
import com.here.platform.data.client.spark.javadsl.VersionedDataConverter;
import com.here.platform.data.client.spark.scaladsl.GroupedData;
import com.here.platform.data.client.spark.scaladsl.VersionedRowMetadata;
import org.apache.spark.api.java.function.MapFunction;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Encoders;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class CarParkingApplicationTest {

    private static final SparkSession sparkSession = SparkSession.builder()
            .appName("NsLs5JIEtf3AzJQFoOSa")
            .master("local[*]")
            .getOrCreate();

    public static void main(String[] args) {
        //writeData();
        //readData();
        deleteData();
    }

    private  static void writeData(){
        // Dataset<Row> inputDF (Input data stored as a DataSet<Row>)
        // HRN catalogHrn (HRN of the output catalog that contains the layer $layerId)
        // String layerId: String (ID of the output index layer)

        List<ParkingSlot> mlist = new ArrayList<>();
//        mlist.add(ParkingSlot.builder()
//                .parkingId("p2")
//                .buildingId("b1")
//                .buildingName("Building1")
//                .numberOfFloors(2)
//                .mt_partition(String.valueOf(Instant.now().toEpochMilli()))
//                .build());

        // val catalogHrn: HRN (HRN of a catalog that contains the layer $layerId)
        // val layerId: String (ID of a versioned layer)
        HRN catalogHrn = HRN.fromString("hrn:here:data::olp-here-hack2020:car-parking");
        String layerId = "parkings";
        Dataset<Row> inputDataFrame = sparkSession.createDataFrame(mlist, ParkingSlot.class);

        JavaLayerDataFrameWriter.create(inputDataFrame)
                .writeLayer(catalogHrn, layerId)
                .withDataConverter(
                        new VersionedDataConverter() {
                            @Override
                            public GroupedData<VersionedRowMetadata> serializeGroup(
                                    VersionedRowMetadata rowMetadata, Iterator<Row> rows) {
                                byte[] bytes = rows.next().getAs("data");
                                return new GroupedData<>(rowMetadata, bytes);
                            }
                        })
                .save();

        sparkSession.stop();
    }

    private  static void readData(){
        HRN catalogHrn = HRN.apply("hrn:here:data::olp-here-hack2020:car-parking");
        String layerId = "parkings";
        Dataset<Row> dataFrame =
                JavaLayerDataFrameReader.create(sparkSession)
                        .readLayer(catalogHrn, layerId)
                        .query(String.format("mt_partition==%s", "4bbb4245-ace9-4af9-9f66-a45f734d805c"))
                        .load();

        dataFrame.printSchema();

        List<String> dataFrameStringContent = dataFrame
                        .map((MapFunction<Row, String>) row -> row.getAs("value"), Encoders.STRING())
                        .collectAsList();

        System.out.println(dataFrameStringContent);
    }

    private static void deleteData(){
        HRN catalogHrn = HRN.fromString("hrn:here:data::olp-here-hack2020:car-parking");
        Dataset<Row> dataFrame =
                JavaLayerUpdater.create(sparkSession)
                        .updateLayer(catalogHrn, "parkings")
                        .delete(String.format("mt_partition=in=(%s,%s,%s,%s,%s)",
                                "123456.1","123456.2","123456.3","123456.4","123456.5"));

        String deleteResult = dataFrame.select("result").first().getString(0);
        int deletedCount = dataFrame.select("count").first().getInt(0);
        String deletionMessage = dataFrame.select("message").first().getString(0);
        System.out.println(deleteResult  + " - " + deletedCount + " - " + deletionMessage);
    }

    private static void listCatalogs(){
        ActorSystem actorSystem = ActorSystem.create();

        DataClient dataClient = DataClient.get(actorSystem);
        AdminApi adminApi = dataClient.adminApi();
        // Now you can start using the API
        adminApi
                .listCatalogs()
                .whenComplete(
                        (catalogHRNs, e) -> {
                            if (catalogHRNs != null) {
                                catalogHRNs.forEach(System.out::println);
                            } else if (e != null) {
                                e.printStackTrace();
                            }
                        })
                // When done, shutdown the Data Client through the ActorSystem
                .thenAccept(
                        unbound ->
                                CoordinatedShutdown.get(actorSystem).run(CoordinatedShutdown.unknownReason()));

    }
}