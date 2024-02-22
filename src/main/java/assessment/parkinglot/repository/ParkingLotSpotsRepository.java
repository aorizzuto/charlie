package assessment.parkinglot.repository;

import assessment.parkinglot.repository.models.ParkingLotSpotsModel;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * We can use this repository to get the total spots for each type
 * This way we can just update the table and we don't need to make a new deploy of the app
 * I will use application-dev.yml parameters for simplicity
 */
public interface ParkingLotSpotsRepository extends JpaRepository<ParkingLotSpotsModel, Long> {
}
