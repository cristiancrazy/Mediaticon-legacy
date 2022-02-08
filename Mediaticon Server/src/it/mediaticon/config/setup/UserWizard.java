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
import it.mediaticon.email.EmailHandler;
import org.apache.commons.mail.Email;

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
					checkFields("Insert Server Address: ", in, addressTest)
			);
		}catch(UnknownHostException ignored){ }

		//Set Server Port
		GlobalConfig.serverPort = Integer.parseInt(checkFields(
			"Insert Server Port (1024~65535): ", in, lPortsTest)
		);

		//Set Telnet Availability
		GlobalConfig.telnetAvailable = Boolean.parseBoolean(
				checkFields("Telnet available (true/false): ", in, boolTest)
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
		//User msg
		System.out.println("Mediaticon Setup [FTP]");

		//Set FTP Availability
		GlobalConfig.ftpAvailable = Boolean.parseBoolean(
				checkFields("FTP Available (true/false): ", in, boolTest)
		);

		//Set FTP Server Address
		try {
			GlobalConfig.ftpAddress = InetAddress.getByName(
					checkFields("FTP Server Address: ", in, addressTest)
			);
		}catch(UnknownHostException ignored){ }

		//Set FTP Server Port
		GlobalConfig.ftpPort = Integer.parseInt(
				checkFields("FTP Server Port (21): ", in, aPortsTest)
		);

		//Set FTP Username and Password
		GlobalConfig.ftpUsername = checkFields("FTP Username: ", in, notEmpty);
		GlobalConfig.ftpPassword = new String(System.console().readPassword("FTP Password: "));

		//End msg
		System.out.println("FTP setup completed successfully");
	}

	/** Manage (locally) server's SMTP settings **/
	public static void smtpSetup(Scanner in){
		//User msg
		System.out.println("Mediaticon Setup [SMTP/EMAIL CLIENT]");

		//Set SMTP Availability
		GlobalConfig.smtpAvailable = Boolean.parseBoolean(
				checkFields("SMTP Available (true/false): ", in, boolTest)
		);

		//Set SMTP Server Address
		try {
			GlobalConfig.smtpAddress = InetAddress.getByName(
					checkFields("SMTP Server Address: ", in, addressTest)
			);
		}catch(UnknownHostException ignored){ }

		//Set SMTP Port
		GlobalConfig.smtpPort = Integer.parseInt(
				checkFields("SMTP Server Port (25/587/465/2525): ", in, aPortsTest)
		);

		//Set SMTP Email Sender - Receiver
		GlobalConfig.fromEmailAddress = checkFields("Email address - sender: ", in, emailTest);
		GlobalConfig.smtpUsername = checkFields("SMTP Username: ", in, notEmpty);
		GlobalConfig.smtpPassword = new String(System.console().readPassword("SMTP Password: "));

		GlobalConfig.toEmailAddress = checkFields("Email address - receiver: ", in, emailTest);

		//Msg
		System.out.println("SMTP setup completed successfully");

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

	/* Test fields */

	//String not empty
	private static final Predicate<String> notEmpty = i -> !(i.isEmpty());

	//InetField test
	private static final Predicate<String> addressTest = i -> {
		try{
			InetAddress.getByName(i);
			return true;
		}catch (UnknownHostException ignored){ }
		return false;
	};

	//Boolean test
	private static final Predicate<String> boolTest = i -> i.matches("True|true|False|false");

	//Port test (limited and all)
	private static final Predicate<String> lPortsTest = i -> {
		try{
			return (Integer.parseInt(i) > 1023) && (Integer.parseInt(i) < 65536);
		}
		catch (NumberFormatException err){
			return false;
		}
	};

	private static final Predicate<String> aPortsTest = i -> {
		try{
			return (Integer.parseInt(i) >= 0) && (Integer.parseInt(i) < 65536);
		}
		catch (NumberFormatException err){
			return false;
		}
	};

	//Email test
	private static final Predicate<String> emailTest = i -> i.matches("^(.+)@(.+)$");
}
