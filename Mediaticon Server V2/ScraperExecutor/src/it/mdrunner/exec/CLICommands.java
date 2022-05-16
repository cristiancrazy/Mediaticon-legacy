/* *******************************************************
 * Author: Cristian Crazy
 * Project date: MAY22
 * Project Website: https://mediaticon.000webhostapp.com
 * -------------------------------------------------------
 * Description:
 * The main purpose of this class is to offer service
 * commands to the CLI Environment (standalone)
 * *******************************************************/


package it.mdrunner.exec;

import it.mdrunner.cfg.PlanLoader;
import it.mdrunner.cfg.SharedConfig;

import java.util.HashMap;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class CLICommands {
	// ======= PRIVATE FIELDS - COMMANDS =======
	/** CLI Environment - Clear screen **/
	protected static void clearScreen(){
		System.out.println("\033[H\033[2J");
		System.out.flush();
	}

	/** CLI Environment - Shutdown **/
	protected static void shutdown(){
		if(PlanLoader.shutdown()){
			System.exit(0);
		}else {
			System.out.println("Closing applications. Wait some seconds, then retry command.");
		}
	}

	/** Show plan - User input needed **/
	protected static void showPlan(Scanner in){
		int index;
		System.out.print("Insert plan ID (index): ");
		try{
			index = Integer.parseInt(in.nextLine());
			System.out.println(PlanLoader.loadedPlanList.get(index));
		}catch (NumberFormatException | NoSuchElementException exc){
			System.out.println("\033[31mInvalid command\033[0m");
		}catch (IndexOutOfBoundsException exc){
			System.out.println("\033[31mInvalid index (Index MAX = " + (PlanLoader.loadedPlanList.size()-1) +")\033[0m");
		}
	}

	/** Show plan - Variable defined and no error handling **/
	@SuppressWarnings("unused")
	protected static void showPlan(int planIndex){
		try{
			System.out.println(PlanLoader.loadedPlanList.get(planIndex));
		}catch (IndexOutOfBoundsException exc){
			System.out.println("\033[31mInvalid index (Index MAX = " + (PlanLoader.loadedPlanList.size()-1) +")\033[0m");
		}
	}

	/** Show all plans **/
	protected static void showAllPlan(){
		for(var i : PlanLoader.loadedPlanList){
			System.out.println(i);
		}
	}

	/** Show config: print all info **/
	protected static void showConfig(){
		SharedConfig.printAllInfo();
	}

	/** CLI Environment: Show commands **/
	protected static void CLIHelp(HashMap<String, Runnable> commandMap){
		System.out.println("\033[32mAvailable commands:\033[0m");
		commandMap.forEach((key, val) -> System.out.println(key));
	}
}
