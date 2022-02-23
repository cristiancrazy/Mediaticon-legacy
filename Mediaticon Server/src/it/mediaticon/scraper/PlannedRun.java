/* ****************************************************************
 * Author: Cristian Capraro
 * Made for MEDIATICON Project, in collaboration with:
 * 1) Giovanni Bellini
 * 2) Emanuele Trento
 * 3) Simone Destro
 *
 * This class is used to define methods to setting up a plan to
 * start scraper at defined Date and Time
 ******************************************************************/

package it.mediaticon.scraper;

import it.mediaticon.config.GlobalConfig;
import it.mediaticon.email.EmailHandler;
import it.mediaticon.exec.MainClass;
import it.mediaticon.ftp.FtpHandler;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;

public class PlannedRun {
    //Loaded config
    static class Plan{
        //Instance variables
        String ScraperName; //-> Name of the scraper
        ChronoUnit Repeat; // -> Repeating cycles (weeks/months/years)
        LocalTime WhenRun; // -> Run in a specified time
        LocalDate DateRun; // -> Run in a specified date
        Path FtpPath;//-> FTP Path
        boolean Enabled; //-> Entry enabled

        //Constructor
        /** Run repeatedly with delay **/
        public Plan(String ScraperName, ChronoUnit repeatEvery, LocalTime whenRun, LocalDate dateRun, boolean Enabled, Path ftpPath){
            this.ScraperName = ScraperName; //Set name of the scraper to run
            this.Repeat = repeatEvery; //Repeat running every ... (Months?)
            this.WhenRun = whenRun; //Run in a specified hours
            this.DateRun = dateRun; //Run in a specified date
            this.Enabled = Enabled; //Set if this entry is enable and it can be executed
            this.FtpPath = ftpPath; //Set ftp path to put the output
        }

        /** Run only once in a specified date/time **/
        public Plan(String ScraperName, LocalTime whenRun, LocalDate dateRun, boolean Enabled, Path ftpPath){ //Run single time
            this.ScraperName = ScraperName;
            this.WhenRun = whenRun;
            this.DateRun = dateRun;
            this.Enabled = Enabled;
            this.FtpPath = ftpPath;
            this.Repeat = ChronoUnit.FOREVER;
        }

        //Methods
        /** Print data **/
        @Override
        public String toString(){
            return "\u001B[31mScraper name: \u001B[0m" + ScraperName + System.lineSeparator() +
                    "\u001B[31mRunning on: \u001B[0m" + WhenRun.format(DateTimeFormatter.ofPattern("HH:mm")) + System.lineSeparator() +
                    "\u001B[31mEach: \u001B[0m" + Repeat.name() + System.lineSeparator() +
                    "\u001B[31m-> Next: \u001B[0m" + DateRun.format(DateTimeFormatter.ofPattern("dd-MM-yyyy")) + System.lineSeparator() +
                    "\u001B[32mFTP set: \u001B[0m" + ((FtpPath != null)? FtpPath.toString() : "Invalid/Not Set") + System.lineSeparator() +
                    "\u001B[31mEnabled: \u001B[0m" + Enabled;
        }
    }

    //Objects
    public static List<Plan> plannedList = new ArrayList<>();


    //Valid fields
    public static final List<String> fileFields = List.of(
            //Scraper information
            "App Name = ", //String name (match with loaded scrapers)
            //Repeating and hours repeat (accept both 12/24hrs)
            "Repeat Every = ", //String type
            "Time = ", //String format: AB:CD
            "Date = ",         //Run in a specified date
            //Actually enabled
            "Plan Enabled = ", //String -> boolean
            //Special tasks
            "FTP = "
    );


    //Static methods to load and export data from/to file
    /** Load planning from configuration file **/
    public static boolean load() {
        //Clear data before reading
        plannedList.clear();

        List<String> read;
        //Read data from planning configuration file
        try{
            read = Files.readAllLines(GlobalConfig.scraperPlan, StandardCharsets.UTF_8); //Read all lines
        }catch(IOException err){
            return false;
        }

        //Analyze file
        if(read.size() >= 1){
            //Flags (to find)
            String AppNameFound = null;
            ChronoUnit RepeatEveryFound = null;
            LocalTime TimeFound = null;
            LocalDate DateFound = null;
            boolean PlanEnabledFound = false;
            Path FtpPath = null;


            boolean startFound = false, atLeastOneStartFound = false;
            for(String inputLine : read){
                //Find start
                if(inputLine.matches("def plan [0-9]{1,4}")){
                    startFound = true; //Set found
                    atLeastOneStartFound = true; //Set found (once)
                }

                //Load analyze
                if(startFound){
                    //Find stop and =============== create the object ====================
                    if(inputLine.matches("end plan config")){

                        //Create new object
                        if((AppNameFound != null)&&(TimeFound != null)&&(DateFound != null)){
                            plannedList.add(((RepeatEveryFound != null)?
                                    //First type
                                    new Plan(AppNameFound, RepeatEveryFound, TimeFound, DateFound, PlanEnabledFound, FtpPath) :
                                    //Second type
                                    new Plan(AppNameFound, TimeFound, DateFound, PlanEnabledFound, FtpPath)));
                        }else{
                            //Notify to user the error
                            System.out.println("\u001B[31mPlan " + (plannedList.size()+1) +" Skipped -> Missing information\u001B[0m");
                        }

                        //Reset startFound
                        startFound = false;
                        //Reset found - fields
                        AppNameFound = null; RepeatEveryFound = null; TimeFound = null; DateFound = null;
                        PlanEnabledFound = false;
                        FtpPath = null; //Clear the list

                    }else { //Analyze file content
                        if(true){ //Match with a valid line TODO:REMOVE THIS LINE -> INDENT

                            //Matching with app name:
                            if(inputLine.startsWith(fileFields.get(0))){
                                if(Loader.getScraperAvailable().stream().anyMatch(str -> str.equals(inputLine.substring(fileFields.get(0).length())))){
                                    AppNameFound = inputLine.substring(fileFields.get(0).length());
                                }else{
                                    return false; //Error occurred - no match with any scraper name
                                }
                            }
                            //Matching with "Repeat Every":
                            if(inputLine.startsWith(fileFields.get(1))){
                                try{
                                    RepeatEveryFound = ChronoUnit.valueOf(inputLine.substring(fileFields.get(1).length()));
                                }catch (IllegalArgumentException err){ //No matching
                                    return false; //Error occurred - no match
                                }

                            }

                            //Matching with "Time": (HH:MM)
                            if(inputLine.startsWith(fileFields.get(2))){
                                String subStr = inputLine.substring(fileFields.get(2).length());
                                if(subStr.matches("[0-9]{2}:[0-9]{2}")){ //Match 24hrs format
                                    //Convert to int
                                    String[] time = subStr.split(":");
                                    try{
                                        int hours = Integer.parseInt(time[0]);
                                        int minutes = Integer.parseInt(time[1]);
                                        if((hours >= 0)&&(hours <= 24)&&(minutes >= 0)&&(minutes <= 59)){
                                            TimeFound = LocalTime.of(hours, minutes);
                                        }else{
                                            throw new Exception("Invalid time format");
                                        }
                                    }catch (Exception err){
                                        return false; //Error occurred - time format invalid or parsing error
                                    }
                                }else{
                                    return false; //Error occurred - time format invalid
                                }
                            }

                            if(inputLine.startsWith(fileFields.get(3))){ //Date
                                String subStr = inputLine.substring(fileFields.get(3).length());
                                if(subStr.matches("[0-9]{1,2}/[0-9]{1,2}/[0-9]{4}")){ //Match 24hrs format
                                    //Convert to int
                                    String[] date = subStr.split("/");
                                    try{
                                        int day = Integer.parseInt(date[0]);
                                        int month = Integer.parseInt(date[1]);
                                        int year = Integer.parseInt(date[2]);
                                        if((day > 0)&&(day <= 31)&&(month >= 1)&&(month <= 12)&&(LocalDate.now().getYear() <= year)){
                                            DateFound = LocalDate.of(year, month, day); //yyyy-mm-dd
                                        }else{
                                            throw new Exception("Invalid date format");
                                        }
                                    }catch (Exception err){
                                        return false; //Error occurred - time format invalid or parsing error
                                    }
                                }else{
                                    return false; //Error occurred - time format invalid
                                }
                            }

                            if(inputLine.startsWith(fileFields.get(4))){ //Plan Enabled
                                PlanEnabledFound = Boolean.parseBoolean(inputLine.substring(fileFields.get(4).length()));
                            }

                            if(inputLine.startsWith(fileFields.get(5))){ // Output to "FTP Forward server" (Path setting)
                                FtpPath = new File(inputLine.substring(fileFields.get(5).length())).toPath();
                            }

                        }else{
                            return false; //Invalid - error occurred - no match
                        }
                    }
                }
            }
            return atLeastOneStartFound;
        }else{
            return false;
        }
    }

    /** Print loaded information: **/
    public static void printPlans(){
        if(!plannedList.isEmpty())
            plannedList.forEach(System.out::println);
        else
            System.out.println("\u001B[31mEmpty\u001B[0m");

        System.out.println();
    }


    /* * ======== Start Planned Server ======== * */
    /** Start plan - Multi-thread required to run **/
    public static void ParsePlans(){
        Timer timer = new Timer();
        for(Plan taskToBeExecuted : plannedList){
            LocalDateTime DateAndTime = LocalDateTime.of(taskToBeExecuted.DateRun, taskToBeExecuted.WhenRun);
            TimerTask tTask = new TimerTask() {
                @Override
                public void run() {
                    System.out.println(taskToBeExecuted.ScraperName + " is now running (planner)");
                    if(Loader.getExitCodeRun(taskToBeExecuted.ScraperName) == 0){ //Success
                        System.out.println("OK");
                        //Upload on ftp
                        if((taskToBeExecuted.FtpPath != null)&&(GlobalConfig.ftpAvailable)){
                            //Get right path
                            String outFile = (taskToBeExecuted.ScraperName.substring(0, 5)) + "_" + LocalDate.now().getYear() + ".csv";
                            Path local = GlobalConfig.outputDir.resolve(outFile);
                            System.out.println("Uploading on FTP");

                            //Output on FTP Server
                            MainClass.executor.execute(() ->{ if(FtpHandler.uploadFile(local, taskToBeExecuted.FtpPath)) System.out.println("UPLOADED ON FTP"); });

                        }

                        //Send email (completed)
                        if(GlobalConfig.smtpAvailable)
                            EmailHandler.sendMail("MD Server - Plan success", "" +
                                    "Dear user,\nThe following plan is ended correctly:\n" + taskToBeExecuted + "\nSincerely," +
                                    "\nYour MD Server.");

                    }else{
                        System.out.println("ERROR");

                        //Send email (completed)
                        if(GlobalConfig.smtpAvailable)
                            EmailHandler.sendMail("MD Server - Plan Failed", "" +
                                    "Dear user,\nThe following plan is ended with ERROR:\n" + taskToBeExecuted + "\nPLEASE CHECK\nSincerely," +
                                    "\nYour MD Server.");

                    }
                }
            };

            MainClass.executor.submit(() -> timer.schedule(tTask ,Date.from(DateAndTime.atZone(ZoneId.systemDefault()).toInstant())));
        }
    }
}
