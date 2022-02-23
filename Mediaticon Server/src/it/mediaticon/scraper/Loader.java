/* ****************************************************************
 * Author: Cristian Capraro
 * Made for MEDIATICON Project, in collaboration with:
 * 1) Giovanni Bellini
 * 2) Emanuele Trento
 * 3) Simone Destro
 *
 * This class is used to manage the 'Scraper directory' and all
 * the scrapers program. In this class there are methods to start
 * and kill processes.
 ******************************************************************/

package it.mediaticon.scraper;

import it.mediaticon.config.GlobalConfig;
import it.mediaticon.exec.MainClass;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

public class Loader {
    //Private fields
    private static List<File> scraperAvailable = new ArrayList<>(); //This list contains all scraper files
    private static Map<String, ProcessBuilder> scraperProcess = new HashMap<>(); //This map contains pre-configured process

    //Private Methods

    /** This initialize and check scraper directory **/
    private static boolean initDir(){
        boolean status = true; //Method status

        scraperAvailable.clear(); //Reset list

        File dir = GlobalConfig.scraperDir.toFile();
        //Check directory
        if(dir.exists()){
            File[] scraper = dir.listFiles();

            if((scraper!=null)&&(scraper.length > 0)){
                //Check file type

                /* Python files */
                scraperAvailable.addAll(Arrays.stream(scraper).filter(i -> i.getName().endsWith(".py")).collect(Collectors.toList()));

                /* Java and Java bytecode files */
                scraperAvailable.addAll(Arrays.stream(scraper).filter(i -> i.getName().endsWith(".java")).collect(Collectors.toList()));
                scraperAvailable.addAll(Arrays.stream(scraper).filter(i -> i.getName().endsWith(".class")).collect(Collectors.toList()));
                scraperAvailable.addAll(Arrays.stream(scraper).filter(i -> i.getName().endsWith(".jar")).collect(Collectors.toList()));

                if(scraperAvailable.isEmpty()) status = false; //If no elements are contained in the list

            }else{
                status = false;
            }

        }else{
            status = false;
        }

        return status;
    }

    /** This method will add information - to start the scraper with the appropriate commands **/
    private static boolean initScraper(int number) throws IndexOutOfBoundsException{
        //Get date
        LocalDate now = LocalDate.now();

        boolean status = false; //Method status

        //Find scraper in the list
        File scraper = scraperAvailable.get(number);

        if(scraper!=null){
            //Check file type and execute the appropriate command to start process
            String scraperName = scraper.getName();

            if(scraperName.endsWith(".py")){ //Python file
                ProcessBuilder pb = new ProcessBuilder(
                        "python3", scraper.getAbsolutePath(),
                        "-y", ""+now.getYear(),
                        "-p", ""+GlobalConfig.outputDir.resolve(Path.of(String.format("%s_%d.csv", scraperName.substring(0, 5), now.getYear()))).toAbsolutePath()
                    );
                pb.redirectOutput(GlobalConfig.logDir.resolve(Path.of(String.format("%s_%d.log", scraperName.substring(0, 5), now.getYear()))).toFile());
                scraperProcess.put(scraperName, pb);
                status = true;
            }

            if(scraperName.endsWith(".jar")){ //Jar compressed file (JAVA and SCALA scraper support)
                ProcessBuilder pb = new ProcessBuilder("java", "-jar", scraper.getAbsolutePath());
                scraperProcess.put(scraperName, pb);
                status = true;
            }

        }
        return status;
    }

    //Public methods

    /** Get scrapers name **/
    public static List<String> getScraperAvailable(){
        return scraperProcess.keySet().stream().toList(); //Convert set to list
    }

    /** This method will start scraper process **/
    public static boolean startScraper(String name){
        Future<Boolean> status = MainClass.executor.submit(
                () -> {
                    try{
                        ProcessBuilder pb = scraperProcess.get(name);
                        Process process = pb.start();
                        process.waitFor();
                        System.out.println("Exit result: " + process.exitValue());
                        return true;
                    }catch (InterruptedException | IOException | NullPointerException ignored){
                        System.out.println("\u001B[31m" + "Process Error Occurred." + "\u001B[0m");
                        return false;
                    }

                }
        );

        //Wait finishing the task submitted and return results if possible
        try{
            return status.get();
        }catch (ExecutionException exc){
            System.out.println("Exec exception " +exc.getMessage());
            return false;
        }catch (InterruptedException exc){
            System.out.println("Exception " + exc.getMessage());
            return false;
        }
    }

    /** Get return code - This method will start scraper process **/
    public static int getExitCodeRun(String name){
        Future<Integer> status = MainClass.executor.submit(
                () -> {
                    try{
                        ProcessBuilder pb = scraperProcess.get(name);
                        Process process = pb.start();
                        process.waitFor();
                        return process.exitValue();
                    }catch (InterruptedException | IOException | NullPointerException ignored){
                        System.out.println("\u001B[31m" + "Process Error Occurred." + "\u001B[0m");
                        return -1; //Error
                    }

                }
        );

        //Wait finishing the task submitted and return results if possible
        try{
            return status.get();
        }catch (ExecutionException exc){
            System.out.println("Exec exception " +exc.getMessage());
            return -1; //Error
        }catch (InterruptedException exc){
            System.out.println("Exception " + exc.getMessage());
            return -1; //Error
        }
    }


    /** This method will start all scraper processes **/
    public static void startScraper(){
        boolean status;
        for(String name : scraperProcess.keySet()){
            status = startScraper(name);
            if(!status){
                System.out.println("Error with " + name);
            }
        }
    }


    //Public Methods

    /** This method will load the scraper directory and to set up
     * scraper's information required to start these programs.*/
    public static boolean loadScrapers(){
        boolean status = initDir(); //Method status

        //init file scraper directory

        if(status){
            //Add correct parameters to start scrapers
            for(int i = 0; i < scraperAvailable.size(); ++i){
                status = initScraper(i);
                if(!status) return false; //Init scraper failed
            }
        }

        return status;
    }

}
