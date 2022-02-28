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
import it.mediaticon.exec.BackgroundService;
import it.mediaticon.exec.MainClass;
import it.mediaticon.scraper.Loader;
import it.mediaticon.scraper.PlannedRun;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Scanner;

public class CommandCLI {
	//Instance variables
	private static boolean psw_encryption = false; //Default

	//Public methods (CLI cmd)

	/* Graphic function -> Clear screen */
	/** Clean CLIs **/
	public static void clear(){
		System.out.println("\033[H\033[2J");
		System.out.flush();
	}

	/** Set server hostname **/
	public static void setHostname(Scanner in){
		System.out.print("\u001B[34mNew server hostname:\u001B[0m ");
		String line = in.nextLine();
		if(!line.isEmpty()){
			GlobalConfig.hostname = line;
		}else{
			System.out.print("\u001B[31mInvalid hostname\u001B[0m ");
		}
	}

	/* Server Setup */

	/** Setting up base network setup and TELNET from Privileged CLI **/
	public static void netSetup(Scanner in){
		UserWizard.networkSetup(in);
		save(in);
	}

	/** Setting up ftp service from Privileged CLI **/
	public static void ftpSetup(Scanner in){
		UserWizard.ftpSetup(in);
		save(in);
	}

	/** Setting up smtp service from Privileged CLI **/
	public static void smtpSetup(Scanner in){
		UserWizard.smtpSetup(in);
		save(in);
	}

	/** Setting up server directories from Admin CLI **/
	public static void dirSetup(Scanner in){
		UserWizard.directorySetup(in);
		save(in);
	}

	/** Encrypt config information on CLIs **/
	public static void privacy_cli(Scanner in){
		System.out.print("\u001B[43m\u001B[30mWARNING\u001B[0m - " +
				"Activate encryption for private data? Y/N: ");
		String answer = in.nextLine();
		//Set result
		psw_encryption = answer.matches("Y|y");

	}

	/** Show the loaded plan for scrapers -> Guest CLI **/
	public static void showPlan(){
		PlannedRun.printPlans();
	}

	/** Verify internet connection: **/
	public static void verifyInternet (){
		System.out.println("\u001B[34mThis process may takes a while to be executed... Please wait.\u001B[0m");
		System.out.println((BackgroundService.verifyConnNow())? "\u001B[32mInternet connection available\u001B[0m" : "\u001B[31mInternet connection unavailable\u001B[0m");
	}

	/** Show actual configuration - submenu **/
	public static void showConfig(Scanner in){
		//Clone Password fields
		String ftpPassword = GlobalConfig.ftpPassword;
		String smtpPassword = GlobalConfig.smtpPassword;

		//Menu Choice
		int choice;
		System.out.println("Categories:");
		System.out.println("1) TELNET Service ");
		System.out.println("2) FTP Service ");
		System.out.println("3) SMTP Service ");
		System.out.println("4) SERVER DIRS ");
		System.out.print("Your choice: ");
		//Check input:
		try{
			choice = Integer.parseInt(in.nextLine());
		}catch(NumberFormatException ignored){
			choice = -1;
		}

		//Password encryption if needed
		if(psw_encryption){
			smtpPassword = "[* Password info unavailable *]";
			ftpPassword = smtpPassword;

		}else{
			ftpPassword = GlobalConfig.ftpPassword;
			smtpPassword = GlobalConfig.smtpPassword;
		}

		//Show config:
		switch (choice) {
			case 1 -> //Telnet and server address
					System.out.println("\u001B[34mNETWORK - MANAGEMENT CONFIG:\u001B[0m" + System.lineSeparator() +
							"Server Address: " + GlobalConfig.serverAddress.getHostAddress() + System.lineSeparator() +
							"Server Port: " + ((GlobalConfig.serverPort == 0) ? "Not set" : GlobalConfig.serverPort) + System.lineSeparator() +
							"Telnet: " + (GlobalConfig.telnetAvailable ? "Yes" : "No")
					);
			case 2 -> //FTP
					System.out.println("\u001B[34mFTP - MANAGEMENT CONFIG:\u001B[0m" + System.lineSeparator() +
							"FTP Forwarding Address: " + GlobalConfig.ftpAddress.getHostAddress() + System.lineSeparator() +
							"FTP Forwarding Port: " + ((GlobalConfig.ftpPort == 21) ? "Default" : GlobalConfig.ftpPort) + System.lineSeparator() +
							"FTP Username/Password: " + GlobalConfig.ftpUsername + "/" + ftpPassword + System.lineSeparator() +
							"FTP: " + (GlobalConfig.ftpAvailable ? "Yes" : "No")
					);
			case 3 -> //SMTP
					System.out.println("\u001B[34mSMTP - MANAGEMENT CONFIG:\u001B[0m" + System.lineSeparator() +
							"SMTP Server Address: " + GlobalConfig.smtpAddress.getHostAddress() + System.lineSeparator() +
							"SMTP Server Port: " + GlobalConfig.smtpPort + System.lineSeparator() +
							"SMTP Username/Password: " + GlobalConfig.smtpUsername + "/" + smtpPassword + System.lineSeparator() +
							"Mailto: " + GlobalConfig.toEmailAddress + " -- from: " + GlobalConfig.fromEmailAddress + System.lineSeparator() +
							"SMTP: " + GlobalConfig.smtpAvailable //Fix here
					);
			case 4 -> //Dirs
					System.out.println("\u001B[34mDIRS - MANAGEMENT CONFIG:\u001B[0m" + System.lineSeparator() +
							"Server config: " + GlobalConfig.serverConf.getParent().toAbsolutePath() + System.lineSeparator() +
							"Scraper: " + GlobalConfig.scraperDir.toAbsolutePath() + System.lineSeparator() +
							"Output: " + GlobalConfig.outputDir.toAbsolutePath() + System.lineSeparator() +
							"Logs: " + GlobalConfig.logDir.toAbsolutePath()
					);
			default -> System.out.println("\u001B[31mInvalid choice\u001B[0m");
		}

	}



	/* Server Configuration Commands */

	/** Save running config to startup config **/
	public static void save(Scanner in){

		System.out.print("\u001B[43m\u001B[30mWARNING\u001B[0m - " +
				"This process will overwrite the server config. Proceed? Y/N: ");
		String answer = in.nextLine();
		if(answer.matches("Y|y")) {

			//Saving
			System.out.print("Saving config to " + GlobalConfig.serverConf + "...");
			System.out.println((ConfigManager.save() ? "[\u001B[32mOK\u001B[0m]" : "[\u001B[31mError\u001B[0m]"));

		}else{
			//Abort reloading
			System.out.println("Aborted.");
		}
	}

	/** Reload plan -> Admin mode **/
	public static void loadPlan(Scanner in){
		System.out.print("\u001B[43m\u001B[30mWARNING\u001B[0m - Proceed to load the PLAN? Y/N: ");
		String answer = in.nextLine();
		if(answer.matches("Y|y")){

			//Reloading

			System.out.println("Loading Plan..." + ((PlannedRun.load())? "" +
					"[\u001B[32mOK\u001B[0m]" :
					"[\u001B[31mError\u001B[0m]" )
			);

		}else{
			//Abort reloading
			System.out.println("Aborted.");
		}
	}

	/** Start planned server -> Admin mode **/
	public static void startPlannedServer(){
		it.mediaticon.scraper.PlannedRun.ParsePlans();
		System.out.println("OK");
	}

	/** Reload startup config **/
	public static void reload(Scanner in){

		System.out.print("\u001B[43m\u001B[30mWARNING\u001B[0m - Proceed to reloading? Y/N: ");
		String answer = in.nextLine();
		if(answer.matches("Y|y")){

			//Reloading
			System.out.println("Loading Startup Config..." + (ConfigManager.load()? "" +
					"[\u001B[32mOK\u001B[0m]" :
					"[\u001B[31mError\u001B[0m]" )
			);

		}else{
			//Abort reloading
			System.out.println("Aborted.");
		}
	}

	/* The following methods will be used to switch from one CLI to another */

	/** Switch to 'more' privileged CLI **/
	public static void enable(Scanner in, Class<? extends GuestCLI> actClass){
		final int MAX_ATTEMPT = 3;

		boolean psw_valid = false;
		try{
			System.out.println("\u001B[41m" + "AUTHENTICATION REQUIRED" + "\u001B[0m");
			for(int i = 0; i < MAX_ATTEMPT&&(!psw_valid); ++i){
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

	/* Scraper Commands */

	/** List scraper available **/
	public static int printScraperAvailable(){
		int count; //Number of scraper available

		System.out.println("\u001B[34m" + "Scraper available: " + "\u001B[0m");

		if((count = Loader.getScraperAvailable().size()) > 0){
			Loader.getScraperAvailable().forEach(scName -> System.out.println("->" + scName));
		}else{
			System.out.println("\u001B[31m" + "No Scraper found." + "\u001B[0m");
		}

		return count;

	}

	/** This command will start a well defined scraper**/
	public static void startScraper(Scanner in){
		if(printScraperAvailable() > 0){
			System.out.println("Insert scraper name: ");
			String input = in.nextLine();
			if(Loader.getScraperAvailable().contains(input)){
				MainClass.executor.submit(() -> Loader.startScraper(input)); //Start scraper
				System.out.println("\u001B[32m" + "Scraper started successfully" + "\u001B[0m");
			}else{
				System.out.println("\u001B[31m" + "Scraper name error" + "\u001B[0m");
			}
		}
	}

	/** This command will setup scraper to run automatically **/
	public static void autoScraperConfig(Scanner in){
		UserWizard.manageScheduling(in);
	}
}