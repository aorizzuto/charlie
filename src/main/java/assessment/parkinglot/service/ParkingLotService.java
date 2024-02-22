package assessment.parkinglot.service;

import assessment.parkinglot.controller.dto.VehicleToPark;
import assessment.parkinglot.enums.ParkingLotAllowedTypes;
import assessment.parkinglot.exceptions.ParkingVehicleException;
import assessment.parkinglot.repository.ParkingLotRepository;
import assessment.parkinglot.repository.models.ParkingLotModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import static assessment.parkinglot.enums.ParkingLotAllowedTypes.*;

@Service
public class ParkingLotService {

    @Value("${parking-lot.spots.total}")
    private long totalSpots;

    @Value("${parking-lot.spots.motorcycle}")
    private long totalMotorcycleSpots;

    @Value("${parking-lot.spots.compact-car}")
    private long totalCompactSpots;

    @Value("${parking-lot.spots.regular-car}")
    private long totalRegularSpots;

    @Value("${parking-lot.spots.min-space-for-van}")
    private int minSpacesForVan;

    @Autowired
    ParkingLotRepository parkingLotRepository;

    private static final Logger logger = LoggerFactory.getLogger(ParkingLotService.class);

    /**
     * Park vehicle in spot if there is free space
     * @param vehicle type of vehicle
     * @return return the saved vehicle in database
     * @throws ParkingVehicleException
     */
    public ParkingLotModel parkVehicle(VehicleToPark vehicle) throws ParkingVehicleException {
        ParkingLotModel parkingLotModel = null;

        ParkingLotAllowedTypes type = ParkingLotAllowedTypes.valueOf(vehicle.getType());

        // We can get a MotorcycleService and CarService if we have more logic for each type
        switch (type) {
            case MOTORCYCLE: parkingLotModel = checkAndSave(type, totalMotorcycleSpots, 1); break;
            case CAR: parkingLotModel = checkAndSave(type, totalCompactSpots + totalRegularSpots, 1); break;
            case VAN: parkingLotModel = checkAndSave(type, totalRegularSpots, minSpacesForVan); break;
            default: logger.info("No valid type");
        }

        return parkingLotModel;
    }

    /**
     * Removing parking lot by ID. If there is a exception will be taken from Controller
     * @param id = id to remove
     */
    public void deleteParkingLot(Long id) {
        parkingLotRepository.deleteById(id);
    }

    /**
     * get spots remaining in total
     * @return return the number of spots remaining
     */
    public long getSpotsRemaining() {
        // The following line is not performant due to if database has millions of records I cannot get
        // a list of million of records. Memory will blow up.
        // The best way to do it is to perform the count in the query itself.

        // 1st way - not performant //////////////////
//        List<ParkingLotModel> allRecords = parkingLotRepository.findAll();
//
//        long spacesOccupied = allRecords.stream()
//                .mapToLong(parkingLot -> {
//                    if (ParkingLotAllowedTypes.VAN.name().equals(parkingLot.getVehicle())) {
//                        return 3L;
//                    } else {
//                        return 1L;
//                    }
//                })
//                .sum();
//        return spacesOccupied;
        // END 1st way

        // 2nd way - best way //////////////////
        Long spacesOccupied = parkingLotRepository.countTotalOccupiedSpaces();
        if (spacesOccupied == null) spacesOccupied = 0L;
        return totalSpots - spacesOccupied;
    }

    /**
     * Return the spots remaining by type of vehicle
     * @param vehicle type of vehicle
     * @return
     */
    public long getSpotsRemainingByType(String vehicle) {
        long freeSpaces = getSpotsRemaining();

        if (freeSpaces == 0) {
            return 0L;
        }

        long spotsRemaining = 0L;
        long totalVehicles = parkingLotRepository.countTotalVehiclesByType(vehicle);

        switch (ParkingLotAllowedTypes.valueOf(vehicle)) {
            case MOTORCYCLE: spotsRemaining = totalMotorcycleSpots - totalVehicles; break;
            case CAR: spotsRemaining = totalRegularSpots + totalCompactSpots - totalVehicles; break;
            case VAN: spotsRemaining = (totalRegularSpots - totalVehicles * minSpacesForVan) / minSpacesForVan; break;
        }

        return spotsRemaining;
    }

    /**
     * Validate is vehicle exist in the expected list
     * @param vehicle param from controller POST mapping
     * @throws ParkingVehicleException
     */
    public void validate(String vehicle) throws ParkingVehicleException {
        try {
            ParkingLotAllowedTypes.valueOf(vehicle);
        } catch (IllegalArgumentException exception) {
            logger.warn("There are no type with name: " + vehicle);
            throw new ParkingVehicleException("There are no type with name: " + vehicle);
        }
    }

    /**
     * Check if there are free space and save vehicle
     * @param type of vehicle
     * @param totalSpots that I have in total for the vehicle type
     * @param minSpacesNeeded min spaces that the vehicle need to park
     * @return
     * @throws ParkingVehicleException
     */
    private ParkingLotModel checkAndSave(ParkingLotAllowedTypes type, long totalSpots, int minSpacesNeeded) throws ParkingVehicleException {
        // Subtract from totalSpaces --> Free spaces
        Long occupiedSpaces;
        long freeSpaces;

        if (type.equals(MOTORCYCLE) || type.equals(VAN)) {
            occupiedSpaces = parkingLotRepository.countTotalVehiclesByType(type.name());
        } else {
            occupiedSpaces = parkingLotRepository.countTotalOccupiedSpacesByCarAndVan();
            if (occupiedSpaces == null) occupiedSpaces = 0L;
        }

        freeSpaces = totalSpots - occupiedSpaces * minSpacesNeeded;

        if (freeSpaces >= minSpacesNeeded) {
            return parkingLotRepository.save(new ParkingLotModel(type.name()));
        } else {
            throw new ParkingVehicleException("There are no spaces left for type: " + type);
        }
    }
}
