public class GPSSensor extends Sensor {
    private float currentLatitude;
    private float currentLongitude;
    private float cachedLatitude;
    private float cachedLongitude;
    private double duration; //time since last signal in hours
    
    private double earthRadius = 3958.8; // in miles
    
    public GPSSensor (String sensorID, String location){
        this.sensorID = sensorID;
        this.location = location;
        this.duration = 1;
        this.currentLatitude = 45;
        this.currentLongitude = 45;
    }
    
    // listens for input from data which must include coordinate data
    public boolean getSensorData(String sensorData) {
        try {
            cachedLatitude = currentLatitude;
            cachedLongitude = currentLongitude;
            currentLatitude = Float.parseFloat(sensorData.split(" ")[1]);
            currentLongitude = Float.parseFloat(sensorData.split(" ")[2]);
            duration = Double.parseDouble(sensorData.split(" ")[3]);
            return true;
        } catch (Exception E){
            return false;
        }
    }
    
    //calculates estimated difference in coordinates
    public double getDistanceDifference(){
        double lat1 = currentLatitude * Math.PI/180;
        double lat2 = cachedLatitude * Math.PI/180;
        double latitudeDifference = (currentLatitude - cachedLatitude) * Math.PI/180;
        double longitudeDifference = (currentLongitude - cachedLongitude) * Math.PI/180.0;
        double a = Math.sin(latitudeDifference/2.0) * Math.sin(latitudeDifference/2) + 
                    Math.cos(lat1) * Math.cos(lat2) *
                    Math.sin(longitudeDifference/2.0) * Math.sin(longitudeDifference/2.0);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        return earthRadius * c; 
    }
    
    //calculates speed of train based on difference in location
    public double getSpeed(){
        return this.getDistanceDifference() / duration;
    }
}