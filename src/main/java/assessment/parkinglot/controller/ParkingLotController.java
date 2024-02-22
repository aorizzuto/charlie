package assessment.parkinglot.controller;

import assessment.parkinglot.enums.ParkingLotAllowedTypes;
import assessment.parkinglot.service.ParkingLotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;

@RestController
@RequestMapping("/parkinglot")
public class ParkingLotController {

    @Autowired
    ParkingLotService parkingLotService;

    @PostMapping("/park")
    public String parkVehicle(@RequestBody VehicleToPark vehicle) {
        try {
            parkingLotService.validate(vehicle.getType());
            parkingLotService.parkVehicle(vehicle);
            return "Vehicle was parked. Vehicle: " + vehicle;
        } catch (Exception exception) {
            return "Vehicle was not parked. There was an issue." +
                    "\nVehicle: " + vehicle +
                    "\nError: " + exception.getMessage() +
                    "\nAllowed types: " + Arrays.toString(ParkingLotAllowedTypes.values())
                    ;
        }
    }
}
