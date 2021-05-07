import java.lang.Math;

public class WheelSensor extends Sensor {
    String axelID;
    private float wheelDiameter; // in INCHES
    private float currentRPM;
    
    //constructor
    public WheelSensor(String sensorID, String location, String axelID, float wheelDiameter){
        this.sensorID = sensorID;
        this.location = location;
        this.axelID = axelID;
        this.wheelDiameter = wheelDiameter;
    }
    
    //listens for input from sensor which must include RPM
    public boolean getSensorData(String sensorData){
        try {
            currentRPM = Float.parseFloat(sensorData.split(" ")[1]);
            return true;
        } catch (Exception E){
            return false;
        }
    }
    
    public float getCurrentRPM(){
        return currentRPM;
    }
    
    //get current speed based on current rpm
    public double getSpeedFromRPM(){
        return wheelDiameter * Math.PI * currentRPM * 60.0 / 63360.0;
    }
}