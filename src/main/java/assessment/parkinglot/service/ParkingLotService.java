package assessment.parkinglot.service;

import assessment.parkinglot.controller.VehicleToPark;
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

    @Value("${parking-lot.spaces.car-van}")
    private Long totalCarVanSpaces;

    @Value("${parking-lot.spaces.motorcycle}")
    private Long totalMotorcycleSpaces;

    @Autowired
    ParkingLotRepository parkingLotRepository;

    private static final Logger logger = LoggerFactory.getLogger(ParkingLotService.class);

    public ParkingLotModel parkVehicle(VehicleToPark vehicle) throws ParkingVehicleException {
        ParkingLotModel parkingLotModel = null;

        // We can get a MotorcycleService and CarService if we have more logic for each type
        switch (ParkingLotAllowedTypes.valueOf(vehicle.getType())) {
            case MOTORCYCLE: parkingLotModel = checkAndSave(MOTORCYCLE, totalMotorcycleSpaces, 1); break;
            case CAR: parkingLotModel = checkAndSave(CAR, totalCarVanSpaces, 1); break;
            case VAN: parkingLotModel = checkAndSave(VAN, totalCarVanSpaces, 3); break;
            default: logger.info("No valid type");
        }

        return parkingLotModel;
    }

    private ParkingLotModel checkAndSave(ParkingLotAllowedTypes type, Long totalSpaces, int minSpacesNeeded) throws ParkingVehicleException {
        // Subtract from totalSpaces --> Free spaces
        Long occupiedSpaces = parkingLotRepository.countTotalOccupiedSpaces(type.name());
        long freeSpaces = totalSpaces - occupiedSpaces * minSpacesNeeded;
        if (freeSpaces >= minSpacesNeeded) {
            return parkingLotRepository.save(new ParkingLotModel(type.name()));
        } else {
            throw new ParkingVehicleException("There are no spaces left for type: " + type);
        }
    }

    public void validate(String vehicle) throws ParkingVehicleException {
        try {
            ParkingLotAllowedTypes.valueOf(vehicle);
        } catch (IllegalArgumentException exception) {
            logger.warn("There are no type with name: " + vehicle);
            throw new ParkingVehicleException("There are no type with name: " + vehicle);
        }
    }
}
