import java.util.*;
import java.text.DecimalFormat;
import java.io.FileNotFoundException;
import java.io.File; 

public class IoTSimulator {
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_WHITE = "\u001B[37m";
    public static final String ANSI_BLUE = "\u001B[34m";         
    
    private static DecimalFormat df = new DecimalFormat("0.0");
    private static DecimalFormat dff = new DecimalFormat("0.00");
    
    private static ArrayList<String[]> users = new ArrayList<String[]>();

    static Dictionary<String, Sensor> sensors = new Hashtable<String, Sensor>();
    
    static boolean isCond = true;
    static boolean loggedIn = false;
    
    private static LoggingSystem logger = new LoggingSystem("log.htr");
    private static RouterSim router;
    
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);        
        try { // open list of users file
            File myObj = new File("users.htr");
            Scanner myReader = new Scanner(myObj);
            while (myReader.hasNextLine()) {
                String data = myReader.nextLine();
                String[] userlog = data.split(" ", 4);
                users.add(userlog);
            }
            myReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("Could not detect local user list");
            e.printStackTrace();
        }
        System.out.print(ANSI_WHITE);
        while (true){ // main login while-true loop
            System.out.println(ANSI_GREEN + "-------Hugs the Rail Onboard Sensor Console--------");
            System.out.print(ANSI_WHITE);            
            System.out.print("Username: "); // ask for username
            System.out.print(ANSI_GREEN);
            String user = sc.next();
            if (user.equals("quit")){ loggedIn=false; return; } // if username == quit, quit program
            System.out.print(ANSI_WHITE);
            System.out.print("Password: "); // ask for password
            System.out.print(ANSI_GREEN);
            String pswd = sc.next();
            for (int x = 0; x < users.size(); x++){ // search user file for valid credentials
                if (users.get(x)[0].equals(user) && users.get(x)[1].equals(pswd)){
                    if (users.get(x)[2].equals("cond")){
                        isCond = true;
                        System.out.println("---Welcome Conductor " + users.get(x)[3] + "---"); // greet with preferred name
                        logger.log(user, "Conductor logged in");
                        loggedIn = true;
                    } else if (users.get(x)[2].equals("tech")){
                        isCond = false;
                        System.out.println("---Welcome Technician " + users.get(x)[3] + "---"); // greet with oreferred name
                        logger.log(user, "Technician logged in");
                        loggedIn = true;
                    }
                }
            }
            
            if (!loggedIn){ // no valid user found, return to login loop
                System.out.println(ANSI_YELLOW + "Username/password combination not found in local database.\nContact a technician to rectify this problem." + ANSI_WHITE);
                logger.log("IOTSYS", "Incorrect login attempt");
            } else {         
                if (isCond) { // conductor branch
                    System.out.print(ANSI_WHITE);                    
                    System.out.println("Reading Global Sensor List...");
                    logger.log("IOTSYS", "Sensor list initialized");  
                    int numGPS = 0, numDRS = 0, numIFS = 0, numCGS = 0, numWS = 0;
                    try {
                        Thread.sleep(1400);
                    } catch (InterruptedException e) {}
                    System.out.print(ANSI_YELLOW);
                    try { // lookup sensor list
                        File myObj = new File("sensors.htr");
                        Scanner myReader = new Scanner(myObj);
                        while (myReader.hasNextLine()) {
                            String data = myReader.nextLine();
                            System.out.println(data);
                            String[] sensorLine = data.split(" ");
                            String ID = sensorLine[0]; 
                            if (ID.equals("WS")){ // ID match what sensors are found
                                sensors.put(sensorLine[1], new WheelSensor(sensorLine[1], sensorLine[2], sensorLine[3], Float.parseFloat(sensorLine[4])));
                                logger.log("IOTSYS", ID + " " + sensorLine[1] + " Wheel Sensor detected");
                                numWS++;
                            } else if (ID.equals("GPSS")){
                                sensors.put(sensorLine[1], new GPSSensor(sensorLine[1], sensorLine[2]));
                                logger.log("IOTSYS", ID + " " + sensorLine[1] + " GPS Sensor detected");                             
                                numGPS++;
                            } else if (ID.equals("CGS")){
                                sensors.put(sensorLine[1], new CrossingGateSensor(sensorLine[1], sensorLine[2]));
                                logger.log("IOTSYS", ID + " " + sensorLine[1] + " Crossing Gate Sensor detected");
                                numCGS++;
                            } else if (ID.equals("IFS")){
                                sensors.put(sensorLine[1], new IceFormationSensor(sensorLine[1], sensorLine[2]));
                                logger.log("IOTSYS", ID + " " + sensorLine[1] + " Ice Formation Sensor detected");
                                numIFS++;
                            } else if (ID.equals("DRS")){
                                sensors.put(sensorLine[1], new DopplerRadarSensor(sensorLine[1], sensorLine[2], Integer.parseInt(sensorLine[3]), Double.parseDouble(sensorLine[4])));
                                logger.log("IOTSYS", ID + " " + sensorLine[1] + " Doppler Radar Sensor detected");
                                numDRS++;
                            } 
                        }
                        myReader.close();
                    } catch (FileNotFoundException e) { // sensor list not found, must restart
                        System.out.print(ANSI_WHITE);
                        System.out.println("Sensor list not found. Provide a sensor list and restart");
                        logger.log("IOTSYS", "Sensor list not found"); 
                        return;
                    }
                    System.out.print(ANSI_GREEN); 
                    System.out.println("---Sensors Detected Onboard---" + ANSI_WHITE); // list detected sensors
                    System.out.print("GPS Sensors:\t\t");
                    if (numGPS<1) {System.out.print(ANSI_YELLOW);}
                    System.out.println(numGPS + ANSI_WHITE);
                    System.out.print("Doppler Radar Sensors:\t");
                    if (numDRS<2) {System.out.print(ANSI_YELLOW);}
                    System.out.println(numDRS + ANSI_WHITE);                    
                    System.out.print("Ice Formation Sensors:\t");
                    if (numIFS<1) {System.out.print(ANSI_YELLOW);}
                    System.out.println(numIFS + ANSI_WHITE);                    
                    System.out.print("Crossing Gate Sensors:\t");
                    if (numCGS<1) {System.out.print(ANSI_YELLOW);}
                    System.out.println(numCGS + ANSI_WHITE);                    
                    System.out.print("Wheel Sensors:\t\t");
                    if (numWS<4) {System.out.print(ANSI_YELLOW);}
                    System.out.println(numWS + ANSI_WHITE);
                    
                    if (numGPS>=1 && numDRS>=2 && numIFS>=1 && numCGS>=1 && numWS>=4){ // count if enough sensors are onboard
                        System.out.println(ANSI_GREEN + "Minimum amount of sensors met!" + ANSI_WHITE);
                    } else {
                        System.out.println(ANSI_YELLOW + "Warning! " + ANSI_WHITE + "Not enough sensors detected to meet minimum requirements");
                    }
                    logger.log("IOTSYS", "Sensors detected");    
                    
                    System.out.print(ANSI_YELLOW + "Begin simulated ride? [y/n] " + ANSI_GREEN); // confirmation to begin script
                    String response = sc.next();
                    if (!(response.equals("y") || response.equals("Y"))){ // confirmation check
                        loggedIn = false;
                        break;
                    }                    
                    
                    System.out.println(ANSI_YELLOW + "---Start of testing script---" + ANSI_WHITE);
                    router = new RouterSim("testscript.htr", 1500, numGPS+numCGS+numDRS+numIFS+numWS); // create router object 
                    
                    double secondsOfHorn = 0;
                    while (true) { // loop for reading sensor data from router
                        String[] block = router.getDataBlock(); // block of sensor data for each sensor
                        if (block == null){
                            System.out.println(ANSI_YELLOW + "---End of testing script---" + ANSI_WHITE);
                            loggedIn = false;
                            logger.log(user, "Conductor logged out");
                            break;
                        } else {
                            ArrayList<Double> speedFromRPM = new ArrayList<Double>();
                            ArrayList<Double> speedFromGPS = new ArrayList<Double>();
                            ArrayList<Double> temperature = new ArrayList<Double>();
                            ArrayList<Double> humidity = new ArrayList<Double>();
                            
                            ArrayList<Double> frontDistance = new ArrayList<Double>();
                            ArrayList<Double> backDistance = new ArrayList<Double>();
                            ArrayList<Double> frontSpeed = new ArrayList<Double>();
                            ArrayList<Double> backSpeed = new ArrayList<Double>();
                            
                            ArrayList<Double> distanceTo = new ArrayList<Double>();
                            
                            boolean lightDetected = false, soundDetected = false, icePossible = false;
                            double wheelSlippageSpeedDiff = 0;
                            
                            for (int i = 0; i<block.length; i++){ // keep looping for sensor data blocks
                                String[] splitblock = block[i].split(" ");
                                Sensor s = sensors.get(splitblock[0]); // do action based on type of sensor
                                if (s instanceof WheelSensor){ // WheelSensor
                                    WheelSensor ws = (WheelSensor) s;
                                    ws.getSensorData(block[i]);
                                    speedFromRPM.add(ws.getSpeedFromRPM());
                                } else if (s instanceof CrossingGateSensor){ // CrossingGateSensor
                                    CrossingGateSensor cgs = (CrossingGateSensor) s;
                                    cgs.getSensorData(block[i]);
                                    lightDetected = lightDetected || cgs.getLightDetected();
                                    soundDetected = soundDetected || cgs.getSoundDetected();
                                    distanceTo.add(cgs.getDistanceTo());
                                } else if (s instanceof GPSSensor) { // GPSSensor
                                    GPSSensor gs = (GPSSensor) s;
                                    gs.getSensorData(block[i]);    
                                    speedFromGPS.add(gs.getSpeed());
                                } else if (s instanceof IceFormationSensor){ // IceFormationSensor
                                    IceFormationSensor ifs = (IceFormationSensor) s;
                                    ifs.getSensorData(block[i]);  
                                    temperature.add(ifs.getTemperature());
                                    humidity.add(ifs.getHumidity());
                                    icePossible = icePossible || ifs.checkFormation(); // if one sensor detects conditions
                                } else if (s instanceof DopplerRadarSensor){ // DopplerRadarSensor
                                    DopplerRadarSensor drs = (DopplerRadarSensor) s;
                                    drs.getSensorData(block[i]);        
                                    if (drs.getPosition() == 1){ // data pertaining to front DRS
                                        if (drs.getReturnFrequency() > 0) { // only add data from sensors that detected something
                                            frontDistance.add(drs.calculateDistance());
                                            frontSpeed.add(drs.calculateSpeedDifference());
                                        }
                                    } else { // data pertaining to back DRS
                                        if (drs.getReturnFrequency() > 0) {
                                            backDistance.add(drs.calculateDistance());
                                            backSpeed.add(drs.calculateSpeedDifference());                                        
                                        }
                                    }
                                }
                            }
                            //print generic information update
                            System.out.println(ANSI_GREEN + "_____________________________________________________");
                            System.out.println(ANSI_WHITE + "Speed:= " + ANSI_GREEN + df.format(findMean(speedFromRPM)) + ANSI_WHITE + " mph (RPM) " + ANSI_GREEN + df.format(findMean(speedFromGPS)) + ANSI_WHITE + " mph (GPS)");
                            System.out.println("Temperature:= " + ANSI_GREEN + df.format(findMean(temperature)) + "\u00B0C" + ANSI_WHITE + " Humidity:= " + ANSI_GREEN + df.format(findMean(humidity)) + "%" + ANSI_WHITE);
                            
                            logger.log("GNRS", df.format(findMean(speedFromRPM)) + " (RPM); " + df.format(findMean(speedFromGPS)) + " (GPS); T: " + df.format(findMean(temperature)) + " C; H: " + df.format(findMean(humidity)) + "%");
                            
                            if (findMean(speedFromRPM)>5){ // check if train is moving fast, RPM is more accurate for slower speeds
                                if (!frontDistance.isEmpty()) { // object detected ahead
                                    if (findMean(frontDistance) <= 5){ // less than 5 miles
                                        System.out.println(ANSI_RED + "[ALERT] Object ahead! " + df.format(findMean(frontDistance)) + " mi. going " + df.format(findMean(speedFromGPS) + findMean(frontSpeed)) + " mph" + ANSI_WHITE);
                                        System.out.println(ANSI_RED + ">>> Signal with horn and brake" + ANSI_WHITE);
                                        
                                        logger.log("ALERT", "Object ahead; " + df.format(findMean(frontDistance)) + " mi.; " + df.format(findMean(speedFromGPS) + findMean(frontSpeed)) + " mph");
                                    } else if (findMean(frontDistance) <= 10){ //less than 10 miles
                                        System.out.println(ANSI_YELLOW + "[INFO] Object ahead! " + df.format(findMean(frontDistance)) + " mi. ahead at " + df.format(findMean(speedFromGPS) + findMean(frontSpeed)) + " mph" + ANSI_WHITE);
                                        System.out.println(ANSI_YELLOW + ">>>Slow down by " + df.format(Math.abs(findMean(frontSpeed))) + " mph" + ANSI_WHITE);
                                        
                                        logger.log("INFO", "Object ahead; " + df.format(findMean(frontDistance)) + " mi.; " + df.format(findMean(speedFromGPS) + findMean(frontSpeed)) + " mph");
                                    }                                    
                                }
                                if (!backDistance.isEmpty()){ // object detected behind
                                    if (findMean(backDistance) <= 5){ // less than 5 miles
                                        System.out.println(ANSI_RED + "[ALERT] Object behind! " + df.format(findMean(backDistance)) + " mi. going " + df.format(findMean(speedFromGPS) + findMean(backSpeed)) + " mph" + ANSI_WHITE);
                                        System.out.println(ANSI_RED + ">>>Signal with horn and speed up" + ANSI_WHITE);
                                        
                                        logger.log("ALERT", "Object behind; " + df.format(findMean(backDistance)) + " mi.; " + df.format(findMean(speedFromGPS) + findMean(backSpeed)) + " mph");
                                    } else if (findMean(backDistance) <= 10){ //less than 10 miles
                                        System.out.println(ANSI_YELLOW + "[INFO] Object behind! " + df.format(findMean(backDistance)) + " mi. going " + df.format(findMean(speedFromGPS) + findMean(backSpeed)) + " mph" + ANSI_WHITE);
                                        System.out.println(ANSI_YELLOW + ">>>Speed up by at least " + df.format(Math.abs(findMean(backSpeed))) + " mph" + ANSI_WHITE);
                                        
                                        logger.log("INFO", "Object behind; " + df.format(findMean(backDistance)) + " mi.; " + df.format(findMean(speedFromGPS) + findMean(backSpeed)) + " mph");
                                    }
                                }
                                
                                if (soundDetected && lightDetected){ // crossing gate detected
                                    if (findMean(distanceTo) <= 3.0 && findMean(distanceTo) > 1.0){ // be aware of gate 3 miles ahead
                                        System.out.println(ANSI_YELLOW + "[INFO] Crossing gate ahead (" + dff.format(findMean(distanceTo)) + " mi.)" + ANSI_WHITE);
                                        System.out.println(ANSI_YELLOW + ">>>Visually confirm gate" + ANSI_WHITE);
                                    } else if (findMean(distanceTo) <= 1.0 && findMean(distanceTo) >= 0.1){ // blow horn for 15 sec
                                        System.out.println(ANSI_YELLOW + "[INFO] Crossing gate close (" + dff.format(findMean(distanceTo)) + " mi.)" + ANSI_WHITE);
                                        if (secondsOfHorn <= 0 && findMean(distanceTo) >= .9){ // do not overrite time left if there is time left1
                                            secondsOfHorn = 15.0;
                                        }
                                    } else { // blow horn for 5 sec
                                        System.out.println(ANSI_YELLOW + "[INFO] Passing crossing gate (" + dff.format(findMean(distanceTo)) + " mi.)"  + ANSI_WHITE);
                                        if (secondsOfHorn <= 0){ // do not overrite time left if there is time left1
                                            secondsOfHorn = 5.0;
                                        }
                                    }
                                    logger.log("INFO", "Crossing gate detected " + dff.format(findMean(distanceTo)));
                                }
                                
                                if (secondsOfHorn > 0){ // check how much longer to blow horn
                                    System.out.println(ANSI_YELLOW + ">>>Blow Horn (" + secondsOfHorn + " sec)" + ANSI_WHITE);
                                    secondsOfHorn -= router.getRefreshRate()/1000.0;
                                }
                                
                                if (icePossible){ // ice can form on tracks
                                    System.out.println(ANSI_YELLOW + "[INFO] Ice formation possible" + ANSI_WHITE);
                                    System.out.println(ANSI_YELLOW + ">>>Slow down and increase traction" + ANSI_WHITE);
                                    logger.log("INFO", "Ice formation possible");                                    
                                }
                                
                                if (Math.abs(findMean(speedFromGPS) - findMean(speedFromRPM)) > 1) { // wheel slippage
                                    if (Math.abs(findMean(speedFromGPS) - findMean(speedFromRPM)) > 5){ // major wheel slippage
                                        System.out.println(ANSI_RED + "[ALERT] Major wheel slippage");
                                        System.out.println(">>>Slow down" + ANSI_WHITE);
                                        logger.log("ALERT", "Major wheel slippage: " + df.format(Math.abs(findMean(speedFromGPS) - findMean(speedFromRPM))));
                                    } else if (Math.abs(findMean(speedFromGPS) - findMean(speedFromRPM)) > 2){ // minor wheel slippage
                                        System.out.println(ANSI_YELLOW + "[INFO] Minor wheel slippage");
                                        System.out.println(">>>Slow down" + ANSI_WHITE);
                                        logger.log("INFO", "Minor wheel slippage: " + df.format(Math.abs(findMean(speedFromGPS) - findMean(speedFromRPM))));
                                    }
                                }
                            }
                        }
                        try {
                            Thread.sleep(router.getRefreshRate());
                        } catch (InterruptedException E) {
                            System.out.println("Couldn't sleep");
                            return;
                        }
                    }
                    loggedIn = false;
                } else { // technician branch
                    while (true){ // technician menu loop
                        System.out.print(ANSI_WHITE); // list menu options
                        System.out.println("Technician menu:\n[$view]\t\t- View Log File\n[$copy]\t\t- Copy Log File to Directory\n[$wipe]\t\t- Remove Log Data\n[$logout]\t- Log Out");
                        System.out.print(ANSI_GREEN);                
                        System.out.print("$");
                        String input = sc.next();
                        if (input.equals("logout")){ // technician log out
                            logger.log(user, "Technician loged out");
                            loggedIn = false;
                            break;
                        } else if (input.equals("view")) { // print all lines in log file
                            logger.log(user, "Technician views log file");
                            System.out.print(ANSI_YELLOW);                            
                            logger.viewLogs();
                            System.out.print(ANSI_WHITE);
                        } else if (input.equals("copy")) { // copy log file to specified location
                            System.out.print(ANSI_WHITE + "Directory to copy log file to: " + ANSI_GREEN);                            
                            String newlogdir = sc.next();
                            System.out.print(ANSI_RED + "Copy log file? [y/n] " + ANSI_GREEN);                            
                            String response = sc.next();
                            if (response.equals("y") || response.equals("Y")){ // confirmation check
                                if (logger.extractLogs(newlogdir)){
                                    System.out.println("Successfully copied log file to " + newlogdir);
                                    logger.log(user, "Technician copied log file to " + newlogdir);
                                } else {
                                    logger.log(user, "Technician attemped to copy log file to " + newlogdir);
                                }
                            }                            
                        } else if (input.equals("wipe")) { // remove data from current log file
                            System.out.print(ANSI_RED + "Wipe log file? [y/n] " + ANSI_GREEN);
                            String response = sc.next();
                            if (response.equals("y") || response.equals("Y")){ // confirmation check
                                logger.log(user , "Technician wiped log file");
                                logger.wipeLogs();      
                                logger.log(user , "Technician wiped log file");
                            }
                        } else { // unknown command
                            System.out.println(ANSI_GREEN + "$" + input + ANSI_WHITE + " - Command not recognized");
                        }
                    }
                } 
            }
        }
    }
    
    private static double findMode(ArrayList<Double> dob){
        double maxValue = 0;
        int maxCount = 0, i, j;

        for (i = 0; i < dob.size(); ++i) {
            int count = 0;
            for (j = 0; j < dob.size(); ++j) {
               if (dob.get(j) == dob.get(i))
               ++count;
            }

            if (count > maxCount) {
               maxCount = count;
               maxValue = dob.get(i);
            }
        }
        return maxValue;        
    }
    
    private static double findMean(ArrayList<Double> dob){
        double out = 0;
        for (int i = 0; i<dob.size(); i++){
            out += dob.get(i);
        }
        return out / dob.size();
    }
    
    private static double findMedian(List<Double> data) {
        if (data.size() % 2 == 0)
            return (data.get(data.size() / 2) + data.get(data.size() / 2 - 1)) / 2;
        else
            return data.get(data.size() / 2);
    }
}