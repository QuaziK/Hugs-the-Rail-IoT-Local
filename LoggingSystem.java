import java.util.*;
import java.io.*;
import java.time.format.DateTimeFormatter;  
import java.time.LocalDateTime;  
import java.nio.file.Files;
import java.nio.file.*;

public class LoggingSystem {
    private String logNameFile;
    private String lastExtractionDate;
    private File file;
    
    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");  
    LocalDateTime now = LocalDateTime.now();  
    
    public LoggingSystem(String fileDir){
        logNameFile = fileDir;        
        file = new File(logNameFile);
        now = LocalDateTime.now();
        lastExtractionDate = dtf.format(now);
    }
    
    //writes to log file
    public void log(String user, String eventData){
        try {
            FileWriter myWriter = new FileWriter(file, true);
            BufferedWriter bw = new BufferedWriter(myWriter);
            now = LocalDateTime.now();
            bw.write(dtf.format(now) + " {" + user + "} " + eventData);
            bw.newLine();
            bw.close();
        } catch (IOException e) {
            System.out.println("Log file " + logNameFile + " not found for logging.");
            e.printStackTrace();
        }
    }
    
    //deletes log file
    public void wipeLogs(){
        try {
            FileWriter myWriter = new FileWriter(file);
            myWriter.write("");
            myWriter.close();
        } catch (IOException e) {
            System.out.println("Log file " + logNameFile + " could not be wiped.");
            e.printStackTrace();
        }       
    }
    
    //copies log file to location
    public boolean extractLogs(String location) {
        try {
            this.log("LOGSYS", "Extracted");
            Path copied = Paths.get(location);
            Path originalPath = Paths.get(logNameFile);
            Files.copy(originalPath, copied, StandardCopyOption.REPLACE_EXISTING);
            now = LocalDateTime.now();
            lastExtractionDate = dtf.format(now);
            return true;
        } catch (IOException E){
            System.out.println("Directory not found");
            return false;
        }
    }
    
    //displays contents of log
    public void viewLogs(){
        try {
            File myObj = new File(logNameFile);
            Scanner myReader = new Scanner(myObj);
            while (myReader.hasNextLine()) {
                String data = myReader.nextLine();
                System.out.println(data);
            }
            myReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("Log file " + logNameFile + " not found for viewing.");
            e.printStackTrace();
        }        
    }
    
    public String getExtractionDate(){
        return lastExtractionDate;
    }
}