/* ****************************************************************
 * Author: Cristian Capraro
 * Made for MEDIATICON Project, in collaboration with:
 * 1) Giovanni Bellini
 * 2) Emanuele Trento
 * 3) Simone Destro
 *
 * The purpose of this class is to store data locally. This data
 * is used and exchanged on the entire project (in different
 * packages).
 ******************************************************************/


package it.mediaticon.config;

import java.net.InetAddress;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;

public class GlobalConfig {


	/* File and Directory Path */

	//Config file PATH
	public static Path serverConf = Path.of("config", "server.conf");

	//Log directory PATH
	public static Path logDir = Path.of("log");

	//Scraper and output directory PATH
	public static Path scraperDir = Path.of("scraper");
	public static Path outputDir = Path.of("output");


	/* Server network configuration */

	//Server address and port
	public static InetAddress serverAddress = InetAddress.getLoopbackAddress();
	public static int serverPort = 0; // 0 -> Automatic port choosing

	//TELNET Service
	public static boolean telnetAvailable = true;
	public static long telnetTimeout = 15000; //This value is in ms

	//FTP Service
	public static boolean ftpAvailable = false;
	public static InetAddress ftpAddress = null;
	public static int ftpPort = 21; //Default port for FTP service
	public static String ftpUsername = null, ftpPassword = null;

	//SMTP (MAIL) Service
	public static boolean smtpAvailable = false;
	public static InetAddress smtpAddress = null;
	public static int smtpPort = 587; //Default port for default configuration
	public static String smtpUsername = null, smtpPassword = null;

	public static String fromEmailAddress = null;
	public static String toEmailAddress = null;

	/* CLI and Security configuration */

	//CLI General options
	public static String hostname = "MEDIATICON CLI";

	//CLI Passwords
	public static Path securityConf = Path.of("config", "security.conf");

	public static Set<String> privilegedPassword = new HashSet<>();
	public static Set<String> adminPassword = new HashSet<>();
}
