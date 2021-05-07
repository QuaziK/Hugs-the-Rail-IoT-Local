public class IceFormationSensor extends Sensor {
    private double currentTemperature; // in celsius
    private double currentHumidity; // percentage
    
    public IceFormationSensor (String sensorID, String location) {
        this.sensorID = sensorID;
        this.location = location;
    }
    
    //listens for input from sensor which must include outside temperature and humidity    
    public boolean getSensorData(String sensorData){
        try {
            currentTemperature = Double.parseDouble(sensorData.split(" ")[1]);
            currentHumidity = Double.parseDouble(sensorData.split(" ")[2]);
            return true;
        } catch (Exception E){
            return false;
        }
    }
    
    public double getTemperature(){
        return currentTemperature;
    }
    
    public double getHumidity(){
        return currentHumidity;
    }
    
    //checks current conditions if ice formation is possible
    public boolean checkFormation(){
        if ( currentTemperature <= 0.0 && currentHumidity >= 72.0 && currentHumidity <= 78.0) {
            return true;
        }
        return false;
    }
}