package assessment.parkinglot.controller.dto;

public class VehicleToPark {
    private String type;

    public String getType() {
        return type;
    }

    @Override
    public String toString() {
        return "VehicleToPark(type=" + type + ")";
    }
}
