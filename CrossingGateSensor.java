public class CrossingGateSensor extends Sensor {
    private boolean lightDetected;
    private boolean soundDetected;
    private double distanceTo; // in miles
    
    public CrossingGateSensor(String sensorID, String location){
        this.sensorID = sensorID;
        this.location = location;
        lightDetected = false;
        soundDetected = false;
    }
    
    //listens for input from sensor which must include RPM
    public boolean getSensorData(String sensorData){
        try {
            if (Integer.parseInt(sensorData.split(" ")[1]) == 1) {
                lightDetected = true;
            } else {
                lightDetected = false;
            }
            
            if (Integer.parseInt(sensorData.split(" ")[2]) == 1) {
                soundDetected = true;
            } else {
                soundDetected = false;
            }        
            
            distanceTo = Double.parseDouble(sensorData.split(" ")[3]);
            
            return true;
        } catch (Exception E) {
            return false;
        }
    }
    
    public boolean getLightDetected(){
        return lightDetected;
    }
    
    public boolean getSoundDetected(){
        return soundDetected;
    }
    
    public double getDistanceTo(){
        return distanceTo;
    }
}