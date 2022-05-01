/* *******************************************************
 * Author: Cristian Crazy
 * Project date: APR22
 * Project Website: https://mediaticon.000webhostapp.com
 * -------------------------------------------------------
 * Description:
 * Fields and methods contained in this class, allows you
 * to get/set/modify and erase some common configuration.
 * The configuration (fields) included here, will be
 * shared between all the classes which are needing these.
 * Every fields contained in this class is at least
 * public and static (feature the single copy of data).
 * *******************************************************/

package it.mdrunner.cfg;

import java.net.InetAddress;
import java.nio.file.Path;

public class SharedConfig {
	// ======[GENERAL APP CONFIGURATION]======

	// #DEPENDENCIES
	public static final String minPython = "3.7.3";

	// #FILES
	public static Path ConfigFile; //This is loaded separately and specify where other settings are located
	public static Path AppConfigFile; //This will be used to specify parameters of scrapers
	public static Path TimePlanFile; //This will be used to specify the default timetable to run scrapers

	// #DIRECTORIES
	public static Path AppFolder;
	public static Path OutputFolder;
	public static Path LogFolder;

	// #FTP SERVICE
	public static boolean FTPEnabled;
	public static InetAddress FTPServer;
	public static int FTPServerPort;
	public static String FTPUser;
	public static String FTPPassword;

	public static Path FTPRootPath;

	// #OUTPUT PRINT METHODS
	public static void printFiles(){
		System.out.println("\033[33m" + "Actual configs file:" + "\033[0m");
		System.out.println("\033[33m" + "Config file: " + "\033[0m" + ConfigFile.toAbsolutePath());
		System.out.println("\033[33m" + "Exe Params file: " + "\033[0m" + AppConfigFile.toAbsolutePath());
		System.out.println("\033[33m" + "Timetable/Plan file: " + "\033[0m" + TimePlanFile.toAbsolutePath());
	}

	public static void printFolders(){
		System.out.println("\033[33m" + "Actual Directories:" + "\033[0m");
		System.out.println("\033[33m" + "App Folder: " + "\033[0m" + AppFolder.toAbsolutePath());
		System.out.println("\033[33m" + "Output Folder: " + "\033[0m" + OutputFolder.toAbsolutePath());
		System.out.println("\033[33m" + "Log Folder: " + "\033[0m" + LogFolder.toAbsolutePath());
	}

	public static void printFTPConfiguration(){
		System.out.println("\033[33m" + "FTP Service Information:" + "\033[0m");
		System.out.println("\033[33m" + "Enabled: " + "\033[0m" + (FTPEnabled? "\033[32mEnabled\033[0m" : "\033[31mDisabled\033[0m"));

		if(FTPEnabled) {
			System.out.println("\033[33m" + "Server: " + "\033[0m" + FTPServer.getHostName() + "\033[33m" + " Port: " + "\033[0m" + FTPServerPort);
			System.out.println("\033[33m" + "Username: " + "\033[0m" + FTPUser);
			System.out.println("\033[33m" + "Password: " + "\033[0m" + ((!FTPPassword.isEmpty())? FTPPassword : "\033[31mUnset\033[0m"));
		}
	}

	public static void printAllInfo(){
		printFiles();
		System.out.println();
		printFolders();
		System.out.println();
		printFTPConfiguration();
	}
}
