/* ****************************************************************
 * Author: Cristian Capraro
 * Made for MEDIATICON Project, in collaboration with:
 * 1) Giovanni Bellini
 * 2) Emanuele Trento
 * 3) Simone Destro
 *
 * This class has been made to load configuration data from
 * external file and save the actual configuration (that can be
 * different) to the config file
 *
 * This class co-work with GlobalConfig class, in this same package
 ******************************************************************/

package it.mediaticon.config;

import java.io.*;
import java.lang.reflect.Field;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.file.Path;
import java.util.List;

public class ConfigManager {
	//Instance variables

	private static final List<String> fileFields = List.of(
			//HOSTNAME
			"Server Hostname = ",
			//SERVER AND TELNET
			"Server Address = ",
			"Server Port = ",
			"Telnet = ",
			"Telnet Timeout = ",
			//DIRECTORIES
			"Scraper Directory = ",
			"Output Directory = ",
			"Log Directory = ",
			//FTP
			"FTP = ",
			"FTP Forwarding Address = ",
			"FTP Forwarding Port = ",
			"FTP Forwarding Username = ",
			"FTP Forwarding Password = ",
			//SMTP - EMAIL
			"SMTP = ",
			"SMTP Server Address = ",
			"SMTP Server Port = ",
			"SMTP Username = ",
			"SMTP Password = ",
			"EMAIL Sender = ",
			"EMAIL Receiver = "
	);

	private static List<Field> localFields;

	static {
		try {
			localFields = List.of(
					//HOSTNAME
					GlobalConfig.class.getField("hostname"),
					//SERVER AND TELNET
					GlobalConfig.class.getField("serverAddress"),
					GlobalConfig.class.getField("serverPort"),
					GlobalConfig.class.getField("telnetAvailable"),
					GlobalConfig.class.getField("telnetTimeout"),
					//DIRECTORIES
					GlobalConfig.class.getField("scraperDir"),
					GlobalConfig.class.getField("outputDir"),
					GlobalConfig.class.getField("logDir"),
					//FTP
					GlobalConfig.class.getField("ftpAvailable"),
					GlobalConfig.class.getField("ftpAddress"),
					GlobalConfig.class.getField("ftpPort"),
					GlobalConfig.class.getField("ftpUsername"),
					GlobalConfig.class.getField("ftpPassword"),
					//SMTP - EMAIL
					GlobalConfig.class.getField("smtpAvailable"),
					GlobalConfig.class.getField("smtpAddress"),
					GlobalConfig.class.getField("smtpPort"),
					GlobalConfig.class.getField("smtpUsername"),
					GlobalConfig.class.getField("smtpPassword"),
					GlobalConfig.class.getField("fromEmailAddress"),
					GlobalConfig.class.getField("toEmailAddress")
			);
		} catch (NoSuchFieldException ignored) { }
	}

	//Methods
	/** Copy server Startup config to Running config **/
	public static boolean load(){
		boolean status = true; //Method status

		String line;
		boolean startFlag = false;
		//Load data from server config file
		try(BufferedReader in = new BufferedReader(new FileReader(GlobalConfig.serverConf.toFile()))){
			//Cycle until the end of file
			while((line = in.readLine()) != null){
				//Check when config starts
				if(line.equals("begin Config")){
					startFlag = true;
					continue;
				}

				//Check fields if startFlag is true
				if(startFlag){

					//Check when config finish
					if(line.equals("end Config")){
						break;
					}

					for(String search : fileFields){
						//On Config line found
						if(line.startsWith(search)){
							try{

								if(localFields.get(fileFields.indexOf(search)).getType().equals(InetAddress.class)){ //InetAddress
									localFields.get(fileFields.indexOf(search)).set(null, InetAddress.getByName(line.substring(search.length())));
								}

								if(localFields.get(fileFields.indexOf(search)).getType().equals(String.class)){ //String
									localFields.get(fileFields.indexOf(search)).set(null, line.substring(search.length()));
								}

								if(localFields.get(fileFields.indexOf(search)).getType().equals(int.class)){ //integer
									localFields.get(fileFields.indexOf(search)).setInt(null, Integer.parseInt(line.substring(search.length())));
								}

								if(localFields.get(fileFields.indexOf(search)).getType().equals(long.class)){ //integer
									localFields.get(fileFields.indexOf(search)).setLong(null, Long.parseLong(line.substring(search.length())));
								}

								if(localFields.get(fileFields.indexOf(search)).getType().equals(boolean.class)){ //boolean
									localFields.get(fileFields.indexOf(search)).setBoolean(null, Boolean.parseBoolean(line.substring(search.length())));
								}

								if(localFields.get(fileFields.indexOf(search)).getType().equals(Path.class)){ //Path - File
									File tmp = new File(line.substring(search.length()));
									localFields.get(fileFields.indexOf(search)).set(null, tmp.toPath());
								}

							}catch(NullPointerException | UnknownHostException | NumberFormatException | IllegalAccessException ignored){
								status = false;
							}

						}
					}

				}
			}

		}catch(IOException ignored){
			status = false;
		}

		//Check if data is valid
		return status&&netCheck()&&emailCheck()&&emailCheck();
	}

	/** Copy server Running Config to Startup Config **/
	public static boolean save(){
		boolean status = true;

		try(BufferedWriter out = new BufferedWriter(new FileWriter(GlobalConfig.serverConf.toFile(), false))){
			out.write("{MEDIATICON SERVER CONFIGURATION}");
			out.newLine();
			out.write("begin Config");
			out.newLine();
			for(String field : fileFields){
				//It can throw NullPointerException if a field's value is 'null'
				try{
					if(localFields.get(fileFields.indexOf(field)).getType().equals(InetAddress.class)){
						InetAddress obj = (InetAddress) (localFields.get(fileFields.indexOf(field)).get(null));
						out.write(field + obj.getHostAddress());
					}else{
						out.write(field + localFields.get(fileFields.indexOf(field)).get(null).toString());
					}
				}catch(NullPointerException exc){
					out.write(field);
				}

				out.newLine();
			}
			out.write("end Config");
			out.newLine();
		}catch(IllegalAccessException | IOException ignored){
			status = false;
		}

		return status;
	}



	//Local methods

	/** Check if the network config read are valid.
	 *	Valid -> do nothing
	 * 	Invalid -> reset to default
	 **/
	private static boolean netCheck(){
		boolean status = true; //Method status

		if(GlobalConfig.serverAddress == null){
			GlobalConfig.serverAddress = InetAddress.getLoopbackAddress(); //Reset address
			status = false;
		}
		if((GlobalConfig.serverPort != 0)&&((GlobalConfig.serverPort < 1024)||(GlobalConfig.serverPort > 65565))){
			GlobalConfig.serverPort = 0; //Reset port
			status = false;
		}
		if(GlobalConfig.telnetTimeout < 0){
			GlobalConfig.telnetTimeout = 15000; //Reset telnet timeout
			status = false;
		}

		return status;
	}

	/** Check if the email config read are valid. **/
	private static boolean emailCheck(){
		boolean status = true; //Method status

		//Check address
		if(GlobalConfig.smtpAddress == null){
			status = false;
		}

		//Check port
		if((GlobalConfig.smtpPort < 0)||(GlobalConfig.smtpPort > 65565)){
			status = false;
		}

		//Enable or Disable SMTP/EMAIL service
		GlobalConfig.smtpAvailable = status;

		return status;
	}

}