/* ****************************************************************
 * Author: Cristian Capraro
 * Made for MEDIATICON Project, in collaboration with:
 * 1) Giovanni Bellini
 * 2) Emanuele Trento
 * 3) Simone Destro
 *
 * This class provides the command line interface for guest users.
 * ****************************************************************/

package it.mediaticon.commands.cli;

import it.mediaticon.commands.CommandCLI;
import it.mediaticon.config.GlobalConfig;

import java.util.*;

public class GuestCLI {
    //CLI variables

    //CLI commands
    protected Map<String, Runnable> command = new HashMap<>();

    protected void commandSet(Scanner in) {
        command.put("help", this::helpCommand); //help -> show possible commands
        command.put("show-scraper", CommandCLI::printScraperAvailable);
        command.put("enable", () -> CommandCLI.enable(in, this.getClass())); //enable -> enter to privileged mode
        command.put("clear", CommandCLI::clear); //Clear screen
        command.put("show-config", () -> CommandCLI.showConfig(in)); //Show actual configuration
        command.put("privacy-mode", () -> CommandCLI.privacy_cli(in)); //Enable\Disable password encryption
        command.put("set-hostname", () -> CommandCLI.setHostname(in)); //Set server hostname
        command.put("verify-net", CommandCLI::verifyInternet); //Verify internet connection
        command.put("show-planning", CommandCLI::showPlan); //Show the server plan (for scrapers)
    }

    protected void helpCommand(){
        List<String> result = command.keySet().stream().toList();
        System.out.println("\u001B[32mCommands available:\u001B[0m");
        result.forEach(System.out::println);
        System.out.println();
    }

    protected void suggestCommand(String cmd){
        List<String> result = new ArrayList<>();
        for(String i : command.keySet())
            if(i.startsWith(cmd)){
                result.add(i);
            }

        if( (!cmd.isEmpty()) && (!result.isEmpty()) ){
            System.out.println("\u001B[31mCommand unrecognized.\u001B[0m");
            System.out.print("\u001B[32mSuggestion: \u001B[0m");
            for(String i : result)
                System.out.print(i + " ");
            System.out.println();
        }else if(!cmd.isEmpty()){
            System.out.println("\u001B[31mCommand unrecognized.\u001B[0m");
        }
    }

    //Constructor
    protected GuestCLI(){ }

    public GuestCLI(Scanner in){
        //Load command set
        commandSet(in);
        String line;

        while(true){
            System.out.print("\u001B[35m" + GlobalConfig.hostname + "> \u001B[0m");
            line = in.nextLine();
            //Search command
            try{
                command.get(line).run();
            }catch(NullPointerException exception){
                suggestCommand(line);
            }

        }
    }
}
