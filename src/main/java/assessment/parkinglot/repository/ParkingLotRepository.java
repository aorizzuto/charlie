package assessment.parkinglot.repository;

import assessment.parkinglot.repository.models.ParkingLotModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ParkingLotRepository extends JpaRepository<ParkingLotModel, Long> {
    @Query(
    "SELECT SUM(CASE WHEN VEHICLE = 'VAN' THEN 3 ELSE 1 END) FROM ParkingLotModel"
    )
    Long countTotalOccupiedSpaces();

    @Query(
            "SELECT SUM(CASE " +
                    "WHEN VEHICLE = 'VAN' THEN 3 " +
                    "WHEN VEHICLE = 'CAR' THEN 1 " +
                    "ELSE 0 " +
                    "END) AS total_count " +
                    "FROM ParkingLotModel"
    )
    Long countTotalOccupiedSpacesByCarAndVan();

    @Query("SELECT COUNT(*) FROM ParkingLotModel WHERE vehicle = :type")
    Long countTotalVehiclesByType(@Param("type") String type);
}

