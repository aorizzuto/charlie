package assessment.parkinglot.controller.dto;

public class VehicleToPark {
    private String type;

    public String getType() {
        return type;
    }

    public VehicleToPark(String type) {
        this.type = type;
    }

    public VehicleToPark() {
        
    }

    @Override
    public String toString() {
        return "VehicleToPark(type=" + type + ")";
    }
}
