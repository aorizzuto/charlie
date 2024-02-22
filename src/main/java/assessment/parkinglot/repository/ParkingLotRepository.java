package assessment.parkinglot.repository;

import assessment.parkinglot.repository.models.ParkingLotModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ParkingLotRepository extends JpaRepository<ParkingLotModel, Long> {

    @Query("SELECT COUNT(*) FROM ParkingLotModel WHERE vehicle = :type")
    Long countTotalOccupiedSpaces(@Param("type") String type);
}

