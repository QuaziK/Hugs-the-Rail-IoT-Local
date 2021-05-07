import java.util.*;
import java.io.*;

public class RouterSim {
    private String scriptName;
    private int refreshRate; //in milliseconds
    private int numSensors;
    private File file;
    private int blockCounter;
    
    ArrayList<String> scriptLines = new ArrayList<String>();
    
    public RouterSim(String scriptName, int refreshRate, int numSensors){
        this.scriptName = scriptName;
        this.refreshRate = refreshRate;
        this.numSensors = numSensors;
        file = new File(scriptName);        
        this.blockCounter = 0;
        
        this.loadArrayList();
    }
    
    // returns an array of all sensor data strings that are sent in a period of time
    public String[] getDataBlock(){
        String[] block = new String[numSensors];
        for (int i = 0; i<numSensors; i++){
            try {
                block[i] = scriptLines.get(blockCounter*numSensors+i);
            } catch (IndexOutOfBoundsException E) {
                return null;
            }
        }
        blockCounter++;
        return block;
    }
    
    //load script into local ArrayList
    private void loadArrayList(){
        try {
            Scanner myReader = new Scanner(file);
            while (myReader.hasNextLine()) {
                String data = myReader.nextLine();
                scriptLines.add(data);
            }
        } catch (IOException e) {
            System.out.println("Router script not found");
            e.printStackTrace();
        }
    }
    
    public int getRefreshRate(){
        return refreshRate;
    }
}