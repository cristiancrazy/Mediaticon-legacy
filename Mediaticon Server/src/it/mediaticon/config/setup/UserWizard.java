/* ****************************************************************
 * Author: Cristian Capraro
 * Made for MEDIATICON Project, in collaboration with:
 * 1) Giovanni Bellini
 * 2) Emanuele Trento
 * 3) Simone Destro
 *
 * This class offers some methods to set-up server configuration
 * from this program.
 ******************************************************************/

package it.mediaticon.config.setup;

import it.mediaticon.config.GlobalConfig;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Scanner;
import java.util.function.Predicate;

public class UserWizard {

	/** Manage (locally) server's network settings **/
	public static void networkSetup(Scanner in){
		//User msg
		System.out.println("Mediaticon Setup [NETWORK]");

		//Set Server Address
		try {
			GlobalConfig.serverAddress = InetAddress.getByName(
					checkFields("Insert Server Address: ", in, i -> {
						try{
							InetAddress.getByName(i);
							return true;
						}catch (UnknownHostException ignored){ }
						return false;
					})
			);
		}catch(UnknownHostException ignored){ }

		//Set Server Port
		GlobalConfig.serverPort = Integer.parseInt(checkFields(
			"Insert Server Port (1024~65535): ", in, i ->
				{
					try{
						return (Integer.parseInt(i) > 1023) && (Integer.parseInt(i) < 65536);
					}
					catch (NumberFormatException err){
						return false;
					}
				}
		));

		//Set Telnet Availability
		GlobalConfig.telnetAvailable = Boolean.parseBoolean(
				checkFields("Telnet available (true/false): ", in, i -> i.matches("True|true|False|false"))
		);

		//Set Telnet Timeout
		GlobalConfig.telnetTimeout = Integer.parseInt(checkFields(
				"Insert Telnet Timeout (>= 0): ", in, i ->
				{
					try{
						return (Integer.parseInt(i) >= 0);
					}
					catch (NumberFormatException err){
						return false;
					}
				}));

		System.out.println("Network setup completed successfully");
	}

	/** Manage (locally) server's FTP settings **/
	public static void ftpSetup(Scanner in){

	}

	/** Manage (locally) server's SMTP settings **/
	public static void smtpSetup(Scanner in){

	}

	/** Manage (locally) server's directories settings **/
	public static void directorySetup(Scanner in){

	}

	//Private methods

	//Simulate a loop if any settings are inappropriate
	private static String checkFields(String msg, Scanner in, Predicate<String> check){
		String line;

		do{
			System.out.print(msg);
		}while(( (!check.test( (line = in.nextLine()) ))));

		return line;
	}

}
