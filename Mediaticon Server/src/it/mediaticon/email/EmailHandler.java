/* ****************************************************************
 * Author: Cristian Capraro
 * Made for MEDIATICON Project, in collaboration with:
 * 1) Giovanni Bellini
 * 2) Emanuele Trento
 * 3) Simone Destro
 *
 * The purpose of this class is to provide email communication, to
 * informing user periodically on the server status or to send
 * messages on possibles server failures or if it's compromised.
 ******************************************************************/

package it.mediaticon.email;

//External dependencies
import it.mediaticon.config.GlobalConfig;
import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.SimpleEmail;

public class EmailHandler {

	/** This methods can be used to send simple emails, it
	 * use Global settings **/
	public static boolean sendMail(String subject, String message){
		boolean status = true;

		Email email = new SimpleEmail();

		// Setting up email server settings
		email.setHostName(GlobalConfig.smtpAddress.getHostName());
		email.setSmtpPort(GlobalConfig.smtpPort);
		email.setStartTLSEnabled(true); //Enable Start-TLS

		//Authentication
		email.setAuthenticator(new DefaultAuthenticator(GlobalConfig.smtpUsername, GlobalConfig.smtpPassword));

		try {
			email.setFrom(GlobalConfig.fromEmailAddress); //Source email
			email.setSubject(subject); //Subject
			email.setMsg(message); //Message
			email.addTo(GlobalConfig.toEmailAddress); //Destination email
			email.send(); //Send the email
		} catch (EmailException ignored) {
			status = false; //Failed
		}

		return status;
	}
}