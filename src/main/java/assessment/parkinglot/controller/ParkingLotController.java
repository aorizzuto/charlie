package assessment.parkinglot.controller;

import assessment.parkinglot.controller.dto.VehicleToPark;
import assessment.parkinglot.enums.ParkingLotAllowedTypes;
import assessment.parkinglot.repository.models.ParkingLotModel;
import assessment.parkinglot.service.ParkingLotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;

@RestController
@RequestMapping("/parkinglot")
public class ParkingLotController {

    @Autowired
    ParkingLotService parkingLotService;

    /**
     * Park vehicle
     * @param vehicle to park
     * @return message success or fail
     */
    @PostMapping("/park")
    public ResponseEntity<String> parkVehicle(@RequestBody VehicleToPark vehicle) {
        try {
            parkingLotService.validate(vehicle.getType());
            ParkingLotModel parkingLotModel = parkingLotService.parkVehicle(vehicle);
            return ResponseEntity.ok("Vehicle was parked. Vehicle: " + parkingLotModel);
        } catch (Exception exception) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(
                    "Vehicle was not parked. There was an issue." +
                    "\nVehicle: " + vehicle +
                    "\nError: " + exception.getMessage() +
                    "\nAllowed types: " + Arrays.toString(ParkingLotAllowedTypes.values())
            );
        }
    }

    /**
     * remove vehicle from parking lot
     * @param id of the vehicle
     * @return message success or fail
     */
    @DeleteMapping("/vehicle/{id}")
    public ResponseEntity<String> vehicleLeave(@PathVariable Long id) {
        try {
            parkingLotService.deleteParkingLot(id);

            return ResponseEntity.ok("Vehicle with id " + id + " left");
        } catch (Exception exception) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(
                    "Vehicle did not leave the parking lot. There was an issue." +
                            "\nVehicle ID: " + id +
                            "\nError: " + exception.getMessage()
            );
        }
    }

    /**
     * give the spots remaining
     * @return message with information
     */
    @GetMapping("/spotsremaining")
    public ResponseEntity<String> spotsRemaining() {
        try {
            long spotsRemaining = parkingLotService.getSpotsRemaining();
            return ResponseEntity.ok("There are " + spotsRemaining + " spots remaining");
        } catch (Exception exception) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(
                    "There was an issue checking spots remaining." +
                            "\nError: " + exception.getMessage()
            );
        }
    }

    /**
     * spots remaining by type of vehicle
     * @param vehicle to search
     * @return message success or fail
     */
    @GetMapping("/spotsremaining/{vehicle}")
    public ResponseEntity<String> spotsRemainingByType(@PathVariable String vehicle) {
        try {
            parkingLotService.validate(vehicle);
            long spotsRemaining = parkingLotService.getSpotsRemainingByType(vehicle);
            return ResponseEntity.ok("There are " + spotsRemaining + " spots remaining for vehicle: " + vehicle);
        } catch (Exception exception) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(
                    "There was an issue checking spots remaining." +
                            "\nError: " + exception.getMessage()
            );
        }
    }
}
