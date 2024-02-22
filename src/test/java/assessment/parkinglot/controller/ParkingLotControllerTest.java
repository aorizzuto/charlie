package assessment.parkinglot.controller;

import assessment.parkinglot.controller.dto.VehicleToPark;
import assessment.parkinglot.exceptions.ParkingVehicleException;
import assessment.parkinglot.repository.models.ParkingLotModel;
import assessment.parkinglot.service.ParkingLotService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;

@SpringBootTest
@AutoConfigureMockMvc
class ParkingLotControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ParkingLotService parkingLotService;

    @ParameterizedTest
    @ValueSource(strings = {"CAR", "MOTORCYCLE", "VAN"})
    void givenParkVehicle_whenAllSuccess_thenNoException(String type) {
        VehicleToPark vehicle = new VehicleToPark(type);
        ParkingLotModel parkingLotModel = new ParkingLotModel();

        assertDoesNotThrow(() -> {
            Mockito.when(parkingLotService.parkVehicle(any())).thenReturn(parkingLotModel);
            mockMvc.perform(MockMvcRequestBuilders.post("/parkinglot/park")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(vehicle)))
                    .andExpect(MockMvcResultMatchers.content().string("Vehicle was parked. Vehicle: " + parkingLotModel))
            ;
        });
    }

    @Test
    void givenParkVehicle_whenThereIsAnError_thenException() {
        VehicleToPark vehicle = new VehicleToPark("CAR");
        ParkingLotModel parkingLotModel = new ParkingLotModel();

        assertDoesNotThrow(() -> {
            Mockito.when(parkingLotService.parkVehicle(any())).thenThrow(new ParkingVehicleException(""));
            mockMvc.perform(MockMvcRequestBuilders.post("/parkinglot/park")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(vehicle)))
                    .andExpect(MockMvcResultMatchers.content().string(
                            "Vehicle was not parked. There was an issue." +
                            "\nVehicle: VehicleToPark(type=CAR)" +
                            "\nError: " +
                            "\nAllowed types: [MOTORCYCLE, CAR, VAN]"))
            ;
        });
    }
}