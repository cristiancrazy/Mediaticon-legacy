/* ****************************************************************
 * Author: Cristian Capraro
 * Made for MEDIATICON Project, in collaboration with:
 * 1) Giovanni Bellini
 * 2) Emanuele Trento
 * 3) Simone Destro
 *
 * This class contains methods that are associated to CLI's server
 * commands.
 ******************************************************************/

package it.mediaticon.commands;

import it.mediaticon.commands.cli.AdminCLI;
import it.mediaticon.commands.cli.GuestCLI;
import it.mediaticon.commands.cli.PrivilegedCLI;
import it.mediaticon.config.ConfigManager;
import it.mediaticon.config.GlobalConfig;
import it.mediaticon.config.SecurityManager;
import it.mediaticon.config.setup.UserWizard;

import java.util.Scanner;

public class CommandCLI {

	/* Graphic function -> Clear screen */
	/** Clean CLIs **/
	public static void clear(){
		System.out.println("\033[H\033[2J");
		System.out.flush();
	}

	/* Server Setup */

	/** Setting up base network setup and TELNET from Privileged CLI **/
	public static void netSetup(Scanner in){
		UserWizard.networkSetup(in);
		save(in);
	}

	/* Server Configuration Commands */

	/** Save running config to startup config **/
	public static boolean save(Scanner in){
		boolean status;

		System.out.print("\u001B[43m\u001B[30mWARNING\u001B[0m - " +
				"This process will overwrite the server config. Proceed? Y/N: ");
		String answer = in.nextLine();
		if(answer.matches("Y|y")) {

			//Saving
			System.out.print("Saving config to " + GlobalConfig.serverConf + "...");
			status = ConfigManager.save();
			System.out.println((status ? "[\u001B[32mOK\u001B[0m]" : "[\u001B[31mError\u001B[0m]"));

		}else{

			//Abort reloading
			System.out.println("Aborted.");
			status = false;

		}
		return status;
	}

	/** Reload startup config **/
	public static boolean reload(Scanner in){
		boolean status = true;

		System.out.print("\u001B[43m\u001B[30mWARNING\u001B[0m - Proceed to reloading? Y/N: ");
		String answer = in.nextLine();
		if(answer.matches("Y|y")){

			//Reloading
			status = ConfigManager.load();
			System.out.println("Loading Startup Config..." + (status? "" +
					"[\u001B[32mOK\u001B[0m]" :
					"[\u001B[31mError\u001B[0m]" )
			);

			}else{

			//Abort reloading
			System.out.println("Aborted.");
			status = false;
		}

		return status;
	}

	/* The following methods will be used to switch from one CLI to another */

	/** Switch to 'more' privileged CLI **/
	public static void enable(Scanner in, Class<? extends GuestCLI> actClass){
		final int MAX_ATTEMP = 3;

		boolean psw_valid = false;
		try{
			System.out.println("\u001B[41m" + "AUTHENTICATION REQUIRED" + "\u001B[0m");
			for(int i = 0; i < MAX_ATTEMP&&(!psw_valid); ++i){
				String psw = new String(System.console().readPassword("Password: "));

				//Encode
				psw = SecurityManager.encodePassword(psw);
				//Verify password
				if(actClass.equals(GuestCLI.class)){ //For Guest
					//2nd type password
					if(GlobalConfig.privilegedPassword.contains(psw)){
						psw_valid = true;
					}

					//1st type password
					if(GlobalConfig.adminPassword.contains(psw)){
						psw_valid = true;
					}

				}else{
					//1st type password
					if(GlobalConfig.adminPassword.contains(psw)){
						psw_valid = true;
					}
				}
			}

			if(!psw_valid) //If password still invalid
				System.out.println("\u001B[31m" + "ACCESS DENIED" + "\u001B[0m");

		}catch(NullPointerException exception){
			//Text message to user

			System.out.println(
					"Server error;" + System.lineSeparator() +
					"Remember, if you are in a simulation, this is normal." +
					System.lineSeparator() +
					"You should use a real console to run this portion of code."
			);

		}

		//Enable next level console
		if(psw_valid){
			if(actClass.equals(GuestCLI.class)) new PrivilegedCLI(in);
			if(actClass.equals(PrivilegedCLI.class)) new AdminCLI(in);
		}
	}

	/* The following methods will be used to regulate CLI's access credentials */

	/** Adding new password - CLI command **/
	public static void addUser(Scanner in){
		SecurityManager.addUser(in);
	}

	/** Show actual number of password loaded and password (encoded) - ADMIN only **/
	public static void printUser(){
		System.out.println("--------------------------------------"); //Separator
		System.out.println("Total password loaded: " + (GlobalConfig.adminPassword.size()+GlobalConfig.privilegedPassword.size()));
		System.out.println("Admin password: " + GlobalConfig.adminPassword.size());
		System.out.println("Privileged password: " + GlobalConfig.privilegedPassword.size());
		System.out.println("--------------------------------------"); //Separator
		//Show privileged (hashed) passwords
		System.out.println("[PRIVILEGED]");
		GlobalConfig.privilegedPassword.forEach(System.out::println);
		System.out.println("--------------------------------------"); //Separator
		//Show admin (hashed) passwords
		System.out.println("[ADMINISTRATOR]");
		GlobalConfig.adminPassword.forEach(System.out::println);
		System.out.println("--------------------------------------"); //Separator
	}
}