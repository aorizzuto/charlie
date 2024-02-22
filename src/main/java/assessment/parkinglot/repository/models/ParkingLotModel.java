package assessment.parkinglot.repository.models;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.LocalDateTime;

@Entity
public class ParkingLotModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String vehicle;
    private LocalDateTime createdTime;

    public ParkingLotModel(String vehicle) {
        this.vehicle = vehicle;
        this.createdTime = LocalDateTime.now();
    }

    @Override
    public String toString() {
        return "ParkingLotModel(id = " + id + ", vehicle = " + vehicle +
                ", createdTime = " + createdTime + ")";
    }
}
