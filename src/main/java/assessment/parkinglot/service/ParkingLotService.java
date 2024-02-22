package assessment.parkinglot.service;

import assessment.parkinglot.controller.VehicleToPark;
import assessment.parkinglot.enums.ParkingLotAllowedTypes;
import assessment.parkinglot.exceptions.ParkingVehicleException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class ParkingLotService {

    private static final Logger logger = LoggerFactory.getLogger(ParkingLotService.class);

    public void parkVehicle(VehicleToPark vehicle) throws ParkingVehicleException {

        logger.info(String.valueOf(vehicle));
        // Get occupied spaces
        // Get Total spaces (from application-dev.yml)
        // Subtract --> Free spaces

        // See if vehicle can park (depends on type)

        // Tables:
        //      - Parked
    }

    public ParkingLotAllowedTypes validate(String vehicle) throws ParkingVehicleException {
        try {
            return ParkingLotAllowedTypes.valueOf(vehicle);
        } catch (IllegalArgumentException exception) {
            logger.warn("There are no type with name: " + vehicle);
            throw new ParkingVehicleException("There are no type with name: " + vehicle);
        }
    }
}
