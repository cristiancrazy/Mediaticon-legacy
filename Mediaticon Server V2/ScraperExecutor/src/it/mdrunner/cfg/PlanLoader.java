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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.*;

public class PlanLoader {
    // ====== [Private and Protected Fields] ======
    protected static HashSet<Integer> PlanID = new HashSet<>(); //Used to prevent cloned data
    public static ArrayList<Plan> loadedPlanList = new ArrayList<>();

    protected static HashMap<String, Timer> timerExe = new HashMap<>();

    // ===== [Actual Running Python Processes and Shutdown Method] =====
    public static ArrayList<Process> RunningPyPS = new ArrayList<>();

    /** Clear loaded and temporary data - delete everything from collections used - Cancel all scheduled tasks
     *  Kill (and wait termination) all running py processes. - Meanwhile run garbage collector **/
    public static boolean shutdown(){

        PlanID.clear();
        loadedPlanList.clear();

        for (String i : timerExe.keySet()) {
            timerExe.get(i).cancel();
        }

        timerExe.clear();
        System.gc();

        if (RunningPyPS.size() > 0) {
            System.out.printf("\u001B[33m Actually running: %s processes\u001B[0m%n", RunningPyPS.size());
        }
        for (Process i : RunningPyPS) {
            try {
                //Kill and wait termination
                i.destroy();
            } catch (Exception exc) {
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

            //Optional
            int toYear = j.optInt("To Year", -1); //Use this year if not found

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
                String MidPath = j.optString("MidPath", "");

                //Get repeatable action
                String repeatSentence = j.optString("Repeat Each", "No");

                Path of = Path.of(MidPath);
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

                    if (!MidPath.equals("")) {
                        if (toYear != -1) { //Create multiple
                            for (int nowYear = year; nowYear <= toYear; ++nowYear, ++ID) {
                                loadedPlanList.add(new Plan(ID, AppName, nowYear, Start, delayN, NextLDTUnit, of));
                            }
                        } else { //Create a single
                            loadedPlanList.add(new Plan(ID, AppName, year, Start, delayN, NextLDTUnit, of));
                        }

                    } else {
                        if (toYear != -1) { //Create multiple
                            for(int nowYear = year; nowYear <= toYear; ++nowYear, ++ID){
                                loadedPlanList.add(new Plan(ID, AppName, nowYear, Start, delayN, NextLDTUnit));
                            }
                        }else{ //Create a single
                            loadedPlanList.add(new Plan(ID, AppName, year, Start, delayN, NextLDTUnit));
                        }
                    }

                }else {
                    //Create Plan - Single time
                    if ((toYear != -1)) { //Create multiple
                        //System.out.println("\033[37m"+ "Test Case OK" + "\033[0m"); //TODO: REMOVE - DEBUG USE
                        for (int nowYear = year; nowYear <= toYear; ++nowYear, ++ID) {
                            loadedPlanList.add(new Plan(ID, AppName, nowYear, Start, of));
                        }
                    } else { //Create a single
                        loadedPlanList.add(new Plan(ID, AppName, year, Start, of));
                    }
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

