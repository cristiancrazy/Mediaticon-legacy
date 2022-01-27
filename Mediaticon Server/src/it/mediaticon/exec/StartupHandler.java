/* ****************************************************************
 * Author: Cristian Capraro
 * Made for MEDIATICON Project, in collaboration with:
 * 1) Giovanni Bellini
 * 2) Emanuele Trento
 * 3) Simone Destro
 *
 * This class includes methods used to startup and init the server
 ******************************************************************/


package it.mediaticon.exec;

import it.mediaticon.config.ConfigManager;
import it.mediaticon.config.GlobalConfig;
import it.mediaticon.config.SecurityManager;

public class StartupHandler {
	/** This method initialize the server **/
	public static void startup(){
		welcome();
		loadConfig();
		loadSecurity();
		loadCLI();
	}

	//Print welcome message
	private static void welcome(){
		System.out.println("\u001B[35mWelcome to MEDIATICON SERVER\u001B[0m");
	}

	//Load config and print status
	private static void loadConfig(){
		boolean status = ConfigManager.load();
		System.out.println("Loading Startup Config..." + (status? "" +
				"[\u001B[32mOK\u001B[0m]" :
				"[\u001B[31mError\u001B[0m]" )
		);

		if(!status){
			System.out.println("\u001B[41m\u001B[30mDefault settings will be applied.\u001B[0m" + System.lineSeparator() +
					"\u001B[33mPlease check file at: " + GlobalConfig.serverConf + "\u001B[0m");
		}
	}

	//Load security
	private static void loadSecurity(){
		boolean status = SecurityManager.loadSecurity();
		System.out.println("Loading Security Config..." + (status? "" +
				"[\u001B[32mOK\u001B[0m]" :
				"[\u001B[31mError\u001B[0m]" )
		);

		if(!status){
			System.out.println("\u001B[33mPlease check file at: " + GlobalConfig.securityConf + "\u001B[0m");
		}
	}

	//Print status before loading CLI
	private static void loadCLI(){
		System.out.println("\u001B[34mNETWORK - MANAGEMENT CONFIG:\u001B[0m\r\n" +
				"Server Address: " + GlobalConfig.serverAddress.getHostAddress() + System.lineSeparator() +
				"Server Port: " + ((GlobalConfig.serverPort == 0) ? "Not set" : GlobalConfig.serverPort)  + System.lineSeparator() +
				"Telnet: " + (GlobalConfig.telnetAvailable? "Yes" : "No")
		);
		System.out.println("\u001B[34mSERVER DIRECTORIES:\u001B[0m\r\n" +
				"Scraper Directory: " + GlobalConfig.scraperDir + System.lineSeparator() +
				"Output Directory: " + GlobalConfig.outputDir + System.lineSeparator() +
				"Log Directory: " + GlobalConfig.logDir
		);
		System.out.println();
		System.out.println("\u001B[34mCLI is now starting...\u001B[0m");

	}
}
