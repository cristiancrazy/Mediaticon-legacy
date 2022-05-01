/* *******************************************************
 * Author: Cristian Crazy
 * Project date: APR22
 * Project Website: https://mediaticon.000webhostapp.com
 * -------------------------------------------------------
 * Description:
 * The main purpose of this class is to load the plan
 * from the plan.json file. It will check if valid and
 * setup all the timer tasks (that will be executed at
 * the specified time).
 * *******************************************************/
package it.mdrunner.cfg;

import it.mdrunner.ftp.FTPHandler;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.nio.file.Path;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.*;

public class PlanLoader {
    // ====== [Private and Protected Fields] ======
    protected static HashSet<Integer> PlanID = new HashSet<>(); //Used to prevent cloned data
    protected static ArrayList<Plan> loadedPlanList = new ArrayList<>();

    protected static HashMap<String, Timer> timerExe = new HashMap<>();

    // ===== [Actual Running Python Processes and Shutdown Method] =====
    public static ArrayList<Process> RunningPyPS = new ArrayList<>();

    /** Clear loaded and temporary data - delete everything from collections used - Cancel all scheduled tasks
     *  Kill (and wait termination) all running py processes. - Meanwhile run garbage collector **/
    public static boolean shutdown(){

        PlanID.clear();
        loadedPlanList.clear();

        for(String i : timerExe.keySet()){
            timerExe.get(i).cancel();
        }

        timerExe.clear();
        System.gc();

        for(Process i : RunningPyPS) {
            try{
                //Kill and wait termination
                i.destroyForcibly().waitFor();
            }catch (Exception exc){
                return false;
            }
        }

        RunningPyPS.clear();
        System.gc();
        return true;
    }

    // ====== [Public Fields] ======

    /** Load and parse plan configuration from plan.json **/
    public static boolean loadPlan(){
        //Out flag
        boolean okFlag = true;

        //Handle input JSON File
        Path file = SharedConfig.TimePlanFile;
        StringBuilder fileBuffer = new StringBuilder();

        try(BufferedReader in = new BufferedReader(new FileReader(file.toFile()))){
            while(in.ready()){
                fileBuffer.append((char) in.read());
            }
        }catch (IOException exc){
            return false; //IO Exception occurred - Method Failed
        }

        JSONArray jsArray = new JSONArray(fileBuffer.toString());

        //Parse plan information fields
        for(int i = 0; i < jsArray.length(); ++i){
            JSONObject j = jsArray.getJSONObject(i);

            //Check ID
            int ID = j.optInt("ID");
            if(PlanID.contains(ID)) continue; //Skip to next plan
            else PlanID.add(ID);

            //Check if Year params is ok
            int year = j.optInt("Year", LocalDateTime.now().getYear()); //Use this year if not found

            try
            {
                //Check App Name (if is valid as loaded application)

                String AppName = j.getString("App Name");
                if(AppLoader.getPyAppsList().stream().noneMatch(ff -> ff.getName().equals(AppName))){ //Check from loaded app
                    throw new JSONException("");
                }

                //Get start date and start time and check format (Part 1 - DATE)
                String StartDate = j.getString("Start Date");
                LocalDate date = LocalDate.parse(StartDate, DateTimeFormatter.ofPattern("dd/MM/yyyy"));

                //Get start date and start time and check format (Part 2 - TIME)
                String StartTime = j.getString("Start Time");
                LocalTime time = LocalTime.parse(StartTime, DateTimeFormatter.ofPattern("HH:mm"));

                //Create LDT Object
                LocalDateTime Start = LocalDateTime.of(date, time);
                if(Start.isBefore(LocalDateTime.now())) throw new JSONException(""); //Remove old plans

                //Get MidPath if available
                String MidPath = j.optString("MidPath");

                //Get repeatable action
                String repeatSentence = j.optString("Repeat Each", "No");

                if(!repeatSentence.equals("No")){ //Repeat actions

                    String[] parsed = repeatSentence.split(" ");
                    int delayN = Integer.parseInt(parsed[0]);

                    if(delayN <= 0) throw new JSONException(""); //Invalid

                    ChronoUnit NextLDTUnit;

                    switch(parsed[1]){ //Check Time information parsed from json file
                        case "Minutes" -> NextLDTUnit = ChronoUnit.MINUTES;

                        case "Hours" -> NextLDTUnit = ChronoUnit.HOURS;

                        case "Days" -> NextLDTUnit = ChronoUnit.DAYS;

                        case "Weeks" -> NextLDTUnit = ChronoUnit.WEEKS;

                        case "Months" -> NextLDTUnit = ChronoUnit.MONTHS;

                        case "Years" -> NextLDTUnit = ChronoUnit.YEARS;

                        default -> throw new JSONException(""); //Invalid information
                    }

                    //Create Plan - Repeatable

                    if(MidPath != null){
                        loadedPlanList.add(new Plan(ID, AppName, year, Start, delayN, NextLDTUnit, Path.of(MidPath)));
                    }else{
                        loadedPlanList.add(new Plan(ID, AppName, year, Start, delayN, NextLDTUnit));
                    }

                }else{
                    //Create Plan - Single time
                    loadedPlanList.add(new Plan(ID, AppName, year, Start));
                }

            }
            catch (DateTimeParseException | NumberFormatException | JSONException exc)
            {
                okFlag = false; //Any of plan are invalid
                PlanID.remove(ID); //Invalid plan -> Remove ID
            }

        }
        return okFlag;
    }

    /** Start all plan **/
    public static void start(){
        for(Plan i : loadedPlanList){
            timerExe.put(i.getAppName(), new Timer(i.getAppName(), false));
            timerExe.get(i.getAppName()).schedule(new TimerTask() {
                @Override
                public void run() {
                    i.run();
                    this.cancel();
                    timerExe.remove(i.getAppName());
                    System.gc();
                }
            }, Date.from(i.getStartTime().atZone(ZoneId.systemDefault()).toInstant()));
        }
    }

}

class Plan implements Runnable{
    // ====== [Private fields] ======
    private final int PlanID;
    private final String AppName;
    private final int yearToScrape;
    private final LocalDateTime StartTime;
    private final boolean Repeatable;

    private LocalDateTime NextRepeat;

    //Next Repeat fields
    private ChronoUnit ChronosNext;
    private int AmountNext;

    //Fields for FTP Service
    private Path MidPath;

    // ====== [Public Fields] ======

    public LocalDateTime getStartTime() {
        return StartTime;
    }

    public String getAppName() {
        return AppName;
    }

    public Plan(int PlanID, String AppName, int yearToScrape, LocalDateTime StartTime, int NextRunLDT, ChronoUnit NextRunLDTUnit){
        this.PlanID = PlanID;
        this.yearToScrape = yearToScrape;
        this.AppName = AppName;
        this.StartTime = StartTime;
        this.Repeatable = true;
        ChronosNext = NextRunLDTUnit;
        AmountNext = NextRunLDT;
        //Calculated NextRepeat
        NextRepeat = StartTime.plus(NextRunLDT, NextRunLDTUnit);
        MidPath = null;
    }

    public Plan(int PlanID, String AppName, int yearToScrape, LocalDateTime StartTime, int NextRunLDT, ChronoUnit NextRunLDTUnit, Path MidPath){
        this.PlanID = PlanID;
        this.yearToScrape = yearToScrape;
        this.AppName = AppName;
        this.StartTime = StartTime;
        this.Repeatable = true;
        ChronosNext = NextRunLDTUnit;
        AmountNext = NextRunLDT;
        //Calculated NextRepeat
        NextRepeat = StartTime.plus(NextRunLDT, NextRunLDTUnit);
        this.MidPath = MidPath;
    }

    @Deprecated(forRemoval = true)
    public Plan(int PlanID, String AppName, int yearToScrape, LocalDateTime StartTime, LocalDateTime NextRepeat){
        this.PlanID = PlanID;
        this.yearToScrape = yearToScrape;
        this.AppName = AppName;
        this.StartTime = StartTime;
        this.Repeatable = true;
        this.NextRepeat = NextRepeat;
    }

    public Plan(int PlanID, String AppName, int yearToScrape, LocalDateTime StartTime){
        this.PlanID = PlanID;
        this.yearToScrape = yearToScrape;
        this.AppName = AppName;
        this.StartTime = StartTime;
        this.Repeatable = false;
    }

    @Override
    public String toString(){
        return  "Plan ID = \033[31m" + PlanID + "\033[0m" + System.lineSeparator() + "App Name = \033[31m" + AppName + "\033[0m" + System.lineSeparator() +
                "Year param: \033[34m" + yearToScrape + "\033[0m" + System.lineSeparator() +
                "Next Run: \033[34m" + StartTime.format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")) + "\033[0m" +System.lineSeparator() +
                "Repeat: \033[33m" + (Repeatable? "Yes" : "No") + "\033[0m" + System.lineSeparator() + "After Next Run: " + (NextRepeat != null ? NextRepeat : "Unset");
    }

    @Override
    public void run() {
        System.out.println("Timer@ Now executing: " + PlanID + " - " +AppName);
        ProcessBuilder process = AppLoader.pyReady(AppName, yearToScrape);
        process.redirectOutput(SharedConfig.LogFolder.resolve(AppName.toLowerCase()+".txt").toFile());

        Thread thread = new Thread(() -> {
            try{
                //Before running, check and delete old files (if are already there)
                File outFile = new File(process.command().get(process.command().size()-1));
                if(outFile.exists()){
                    boolean deleted = outFile.delete();
                    if(!deleted) throw new IOException("Old File found - But can't delete.");
                }

                Process ps = process.start();
                PlanLoader.RunningPyPS.add(ps); //Add this PS to currently running list
                ps.waitFor(); //Wait ends
                if(ps.exitValue() != 0){
                    System.out.println("\033[31mTimer@ Execution: " + PlanID + " - " + AppName + System.lineSeparator() + "Ended with error.\033[0m"
                    +System.lineSeparator() + "Exit Code: " + ps.exitValue());

                    //Remove this PS from currently running list
                    PlanLoader.RunningPyPS.remove(ps);

                    throw new IOException("Failed Scraper - Exit Code: " + ps.exitValue());
                }
                else{
                    System.out.println("\033[32mTimer@ Execution: " + PlanID + " - " + AppName + System.lineSeparator() + "Ended correctly.\033[0m");

                    //Remove this PS from currently running list
                    PlanLoader.RunningPyPS.remove(ps);

                    //Upload on FTP Off-Shore Server
                    if(SharedConfig.FTPEnabled){
                        Path local = outFile.toPath();
                        Path remote;

                        if(MidPath != null) //MidPath is an override field
                            remote = MidPath.resolve(local.getFileName());
                        else
                            remote = local.getFileName();

                        System.out.println("\033[34mTimer @ FTP Service (Uploading)\033[0m");
                        System.out.println("Local Path: " + local.toAbsolutePath() + System.lineSeparator() + "Remote Path: " + remote);

                        if(FTPHandler.uploadFile(local, remote)){
                            System.out.println("\033[32mTimer @ FTP Service: UPLOAD OK\033[0m");
                        }else{
                            System.out.println("\033[31mTimer @ FTP Service: UPLOAD FAIL\033[0m");
                            throw new IOException("Scraper OK. FTP Upload Failed.");
                        }

                    }

                }

            }catch (InterruptedException | IOException exc){
                Path logFile = SharedConfig.LogFolder.resolve("FAIL_"+AppName.toLowerCase()+".txt");
                try(BufferedWriter log = new BufferedWriter(new FileWriter(logFile.toFile(), true))){
                    log.write("[" + LocalDateTime.now() + "]--> " + "Error occurred with a scraper.");
                    log.newLine();
                    log.write(exc.getMessage());
                    log.newLine();
                    log.write("Plan ID = " + PlanID + "\t" + "Year = " + yearToScrape);
                    log.newLine();
                    log.write("Scraper initialized with the following arguments: ");
                    log.newLine();
                    log.write(process.command().toString());
                    log.flush(); //Flush the stream
                    for(int i = 0; i < 3; ++i) log.newLine(); //Separate errors
                }catch (IOException ignored){ }
            }finally{
                //Reschedule
                if(Repeatable){
                    PlanLoader.timerExe.remove(AppName);
                    PlanLoader.timerExe.put(AppName, new Timer(AppName, false));

                    System.out.println("\033[34mTimer@ Execution: " + PlanID + " - " + AppName);
                    System.out.println("\033[34mNext run: " + NextRepeat + "\033[0m");
                    System.out.println("\033[33mAfter Next run: " + NextRepeat.plus(AmountNext, ChronosNext) + "\033[0m");

                    PlanLoader.timerExe.get(AppName).schedule(new TimerTask() {

                        @Override
                        public void run() {
                            if(MidPath != null) //Check if MidPath is already set
                                new Plan(PlanID, AppName, yearToScrape, NextRepeat, AmountNext, ChronosNext, MidPath).run();
                            else //Init without MidPath (unnecessary)
                                new Plan(PlanID, AppName, yearToScrape, NextRepeat, AmountNext, ChronosNext).run();
                        }
                    }, Date.from(NextRepeat.atZone(ZoneId.systemDefault()).toInstant()));
                }
            }
        });
        thread.start();
    }
}