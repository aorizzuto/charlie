package assessment.parkinglot.service;

import assessment.parkinglot.controller.dto.VehicleToPark;
import assessment.parkinglot.exceptions.ParkingVehicleException;
import assessment.parkinglot.repository.ParkingLotRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static assessment.parkinglot.enums.ParkingLotAllowedTypes.*;
import static org.mockito.ArgumentMatchers.anyString;

@ExtendWith(MockitoExtension.class)
class ParkingLotServiceTests {

    @InjectMocks
    private ParkingLotService parkingLotService;
    @Mock
    private ParkingLotRepository parkingLotRepository;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(ParkingLotServiceTests.class);
        getReflection();
    }

    @ParameterizedTest
    @ValueSource(strings = {"MOTORCYCLE", "VAN"})
    void givenVehicleToPark_whenIsMotorcycleOrCar_thenExpectCorrectRepositoryMethodCalledAndExpectSuccess(String type) {
        // GIVEN
        VehicleToPark vehicle = new VehicleToPark(type);
        Mockito.when(parkingLotRepository.countTotalVehiclesByType(anyString())).thenReturn(1L);

        // WHEN
        Assertions.assertDoesNotThrow(() -> parkingLotService.parkVehicle(vehicle));

        // THEN
        Mockito.verify(parkingLotRepository, Mockito.times(1)).countTotalVehiclesByType(anyString());
    }

    @Test
    void givenVehicleToPark_whenIsCar_thenExpectCorrectRepositoryMethodCalledAndExpectSuccess() {
        // GIVEN
        VehicleToPark vehicle = new VehicleToPark(CAR.name());
        Mockito.when(parkingLotRepository.countTotalOccupiedSpacesByCarAndVan()).thenReturn(1L);

        // WHEN
        Assertions.assertDoesNotThrow(() -> parkingLotService.parkVehicle(vehicle));

        // THEN
        Mockito.verify(parkingLotRepository, Mockito.times(1)).countTotalOccupiedSpacesByCarAndVan();
    }

    @Test
    void givenVehicleToPark_whenThereIsNoMoreSpace_thenExpectException() {
        // GIVEN
        VehicleToPark vehicle = new VehicleToPark(VAN.name());
        Mockito.when(parkingLotRepository.countTotalVehiclesByType(anyString())).thenReturn(3L);

        // WHEN
        Exception exception = Assertions.assertThrows(Exception.class, () -> parkingLotService.parkVehicle(vehicle));
        Assertions.assertEquals("There are no spaces left for type: VAN", exception.getMessage());

        // THEN
        Mockito.verify(parkingLotRepository, Mockito.times(1)).countTotalVehiclesByType(anyString());
    }

    @Test
    void givenSpotsRemainingCall_whenReturnIsNull_thenExpectZero() {
        // GIVEN
        Mockito.when(parkingLotRepository.countTotalOccupiedSpaces()).thenReturn(null);

        // WHEN
        Long current = Assertions.assertDoesNotThrow(() -> parkingLotService.getSpotsRemaining());

        // THEN
        Assertions.assertEquals(25, current);
    }

    @Test
    void givenSpotsRemainingCall_whenReturnIsNotNull_thenExpectValue() {
        // GIVEN
        Mockito.when(parkingLotRepository.countTotalOccupiedSpaces()).thenReturn(5L);

        // WHEN
        Long current = Assertions.assertDoesNotThrow(() -> parkingLotService.getSpotsRemaining());

        // THEN
        Assertions.assertEquals(20, current);
    }

    @ParameterizedTest
    @ValueSource(strings = {"MOTORCYCLE", "CAR"})
    void givenSpotsRemainingByType_whenIsMotorcycleOrCar_thenExpectValue(String type) {
        // GIVEN
        Mockito.when(parkingLotRepository.countTotalOccupiedSpaces()).thenReturn(0L);
        Mockito.when(parkingLotRepository.countTotalVehiclesByType(type)).thenReturn(3L);

        // WHEN
        Long current = Assertions.assertDoesNotThrow(() -> parkingLotService.getSpotsRemainingByType(type));

        // THEN
        if (type.equals("MOTORCYCLE")) {
            Assertions.assertEquals(2, current);
        } else {
            Assertions.assertEquals(17, current);
        }
    }

    @Test
    void givenSpotsRemainingByType_whenIsVan_thenExpectValueDividedBy3() {
        // GIVEN
        Mockito.when(parkingLotRepository.countTotalOccupiedSpaces()).thenReturn(1L);
        Mockito.when(parkingLotRepository.countTotalVehiclesByType(VAN.name())).thenReturn(3L);

        // WHEN
        Long current = Assertions.assertDoesNotThrow(() -> parkingLotService.getSpotsRemainingByType(VAN.name()));

        // THEN
        Assertions.assertEquals(0, current);
    }

    @ParameterizedTest
    @ValueSource(strings = {"BLA", "MOTO", "BICYCLE", "FOOT", "BOAT"})
    void givenType_whenIsInvalidType_thenException(String type) {
        // WHEN
        Exception exception = Assertions.assertThrows(ParkingVehicleException.class, () -> parkingLotService.validate(type));

        // THEN
        Assertions.assertEquals("There are no type with name: " + type, exception.getMessage());
    }

    private void getReflection() {
        ReflectionTestUtils.setField(parkingLotService, "totalSpots", 25L);
        ReflectionTestUtils.setField(parkingLotService, "totalMotorcycleSpots", 5);
        ReflectionTestUtils.setField(parkingLotService, "totalCompactSpots", 10);
        ReflectionTestUtils.setField(parkingLotService, "totalRegularSpots", 10);
        ReflectionTestUtils.setField(parkingLotService, "minSpacesForVan", 3);
    }

}