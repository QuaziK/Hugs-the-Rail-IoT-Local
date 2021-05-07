public class DopplerRadarSensor extends Sensor {
    int position; // 1 if positioned the front, 2 if positioned at the back
    double sentFrequency; // frequency of sent signal
    double returnFrequency;
    double duration;
    double speedOfWave = 186282.397; // miles per second
    
    public DopplerRadarSensor (String sensorID, String location, int position, double frequency){
        this.sensorID = sensorID;
        this.location = location;
        this.position = position;
        this.sentFrequency = frequency;
        this.duration = Double.POSITIVE_INFINITY;
    }
    
    //listens for input from sensor which must include return frequency and pulse duration    
    public boolean getSensorData(String sensorData){
        try {
            returnFrequency = Double.parseDouble(sensorData.split(" ")[1]);
            duration = Double.parseDouble(sensorData.split(" ")[2]);
            return true;
        } catch (Exception E) {
            return false;
        }
    }
    
    //calculates distance to object based on duration of pulse
    public double calculateDistance(){
        return duration*speedOfWave;
    }
    
    //calculate relative speed of object based on frequency modulation
    public double calculateSpeedDifference(){     
        return (speedOfWave * (sentFrequency - returnFrequency) / sentFrequency);
    }
    
    public int getPosition(){
        return position;
    }
    
    public double getSentFrequency(){
        return sentFrequency;
    }
    
    public double getReturnFrequency(){
        return returnFrequency;
    }
    
    public double getDuration(){
        return duration;
    }
}