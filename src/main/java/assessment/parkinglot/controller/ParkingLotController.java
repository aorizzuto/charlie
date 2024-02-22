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
