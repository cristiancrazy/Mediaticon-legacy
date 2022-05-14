/* *******************************************************
 * Author: Cristian Crazy
 * Project date: APR22
 * Project Website: https://mediaticon.000webhostapp.com
 * Author Website: https://me.cristiancrazy.it
 * -------------------------------------------------------
 * Co-Authors:
 * > VISUAL LASER (His Website: https://shorturl.at/jrwM5)
 * > GIO-BELL (S.P. https://shorturl.at/etzJP)
 * > SIMO-IL-NAVY (S.P. https://shorturl.at/dnzLT)
 * -------------------------------------------------------
 * Made for MD™ PROJECT - PLEASE DO NOT SHARE SOURCES!!!
 * Every illegal action will be severely punished.
 * -------------------------------------------------------
 * Description:
 * This class contains the entry point of the program,
 * which will load settings from the config.json file, and
 * then it will load the time planning and the executable
 * folder, and in the end, it will execute and upload the
 * result on the private "FTP Forward Server" (only if
 * it is explicitly enabled by the config.json file).
 * *******************************************************/

package it.mdrunner.exec;

import it.mdrunner.cfg.AppLoader;
import it.mdrunner.cfg.ConfigLoader;
import it.mdrunner.cfg.SharedConfig;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;

public class MainExec {
	//CLI Environment flag
	private static boolean CLIEnabled = false;
	public static void main(String[] params) {
		showBrand(params); //Show brand before initializing the application
		initApp(params); //Load config, parse params, and print all info
		AppLoader.initPython(); //Init python executables

		//Execute a minimal CLI environment
		if(CLIEnabled) CLIEnvironment.CLIThread.start();
	}



	// This method will load the application logo. It will be shown on the screen before loading
	private static void showBrand(String[] params){
		if(params.length > 3){
			if(params[2].equals("-l")||params[2].equals("/l")){ // "-l" Stands for the logo
				Path logoPath = new File(params[3]).toPath();
				if(logoPath.toFile().exists()&&logoPath.toFile().isFile()){

					StringBuilder data = new StringBuilder(); //Read char logo
					try(BufferedReader in = new BufferedReader(new FileReader(logoPath.toFile()))){
						while(in.ready()) data.append((char) in.read());
					}catch(IOException ignored){ }

					if(!data.isEmpty()) System.out.println(data); //Print logo
					try{
						Thread.sleep(1000); //Wait 1 sec before continue.
					}catch (InterruptedException ignored){ }
				}
			}
		}
	}

	// This method will init the application
	private static void initApp(String[] params){
		//Params parsing
		if(params.length > 0){
			if (params[0].equals("-g")) {
				PlanGenerator.generatePlan(params);
				System.exit(0);
			}

			if ((params[0].startsWith("-c") || params[0].startsWith("/c")) && (!params[1].isEmpty())) {
				if (params[0].endsWith("e")) { //Stands for "environment"
					CLIEnabled = true; //Activate a minimal environment
				}
				Path configPath = new File(params[1]).toPath();
				if (configPath.toFile().exists() && configPath.toFile().isFile()) {
					System.out.println("Found config. Now, loading settings from configuration file.");
					//Set config file to shared configuration
					SharedConfig.ConfigFile = configPath;
				}else{
					System.out.println("You must specify where is located the config file.");
					System.out.println("\033[31mInvalid path,\033[0m Exiting.");
					System.exit(254); // IO Exit
				}
			}else{
				System.out.println("You must specify where is located the config file.");
				System.out.println("\033[33mExample:\033[0m ScraperExecutor.jar -c config/config.json ");
				System.out.println("\033[31mMissing config file,\033[0m Exiting.");
				System.exit(255); // Missing minimum requirements
			}
		}else{
			System.out.println("You must specify where is located the config file.");
			System.out.println("\033[31mMissing config file,\033[0m Exiting.");
			System.exit(255); // Missing minimum requirements
		}

		//Load data
		if(ConfigLoader.loadConfigFile()){
			System.out.println("\033[32mConfiguration Loaded Correctly.\033[0m");
		}else{
			System.out.println("\033[31mError inside the configuration file.\033[0m Exiting.");
			System.exit(253); // Content Missing or invalid exit
		}

		SharedConfig.printAllInfo();
	}
}
