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

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class PlannedRun {

    //Static methods to load and export data from/to file
    /** Load planning from configuration file **/
    public static boolean load() {
        //Read data from planning configuration file
        try(BufferedReader in = new BufferedReader(new FileReader(GlobalConfig.scraperPlan.toFile()))){
            String line;

            //Reading each lines
            while((line = in.readLine())!=null){
                //Check a valid start
                if(line.matches("#[0-9]{1,4} plan")){
                    while( ((line = in.readLine()) != null) && (!line.equals("end plan"))){

                    }
                }
            }
            return true;
        }catch(IOException err){
            return false;
        }
    }
}
