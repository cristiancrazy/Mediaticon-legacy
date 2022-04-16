/* *******************************************************
 * Author: Cristian Crazy
 * Project date: APR22
 * Project Website: https://mediaticon.000webhostapp.com
 * -------------------------------------------------------
 * Description:
 * This class provides shorthand methods to check and
 * upload files on an Off-Shore FTP Server.
 * It works with FTP Authentication (so, must be provided
 * username and password).
 * *******************************************************/

package it.mdrunner.ftp;

import it.mdrunner.cfg.SharedConfig;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;

import java.io.*;
import java.nio.file.Path;


public class FTPHandler {

	/** Upload file via ftp connection
	 *  This method returns a 'true' boolean value if operation
	 *  is completed successfully **/
	public static boolean uploadFile(Path localFile, Path remoteFile){
		boolean status;

		FTPClient client = new FTPClient();
		try{

			//Connection to FTP
			client.connect(SharedConfig.FTPServer, SharedConfig.FTPServerPort);
			client.login(SharedConfig.FTPUser, SharedConfig.FTPPassword);

			client.enterLocalPassiveMode(); //Enable passive mode

			//File management
			InputStream inFile = new FileInputStream(localFile.toFile());
			client.setFileType(FTP.BINARY_FILE_TYPE);

			//Loading file and set status
			status = client.storeFile(remoteFile.toFile().getPath().replace('\\', '/'), inFile);

			//Close streams and disconnect FTP
			inFile.close();
			client.logout();
			client.disconnect();

		}catch(IOException exception){
			System.out.println(exception.getMessage());
			status = false;
		}

		return status;
	}

	/** Download file via ftp connection
	 * This method returns a 'true' boolean value
	 * if operation is completed successfully **/
	@SuppressWarnings("unused")
	public static boolean downloadFile(Path remoteFile, Path localFile){
		boolean status;

		FTPClient client = new FTPClient();
		try{

			//Connection to FTP
			client.connect(SharedConfig.FTPServer, SharedConfig.FTPServerPort);
			client.login(SharedConfig.FTPUser, SharedConfig.FTPPassword);

			client.enterLocalPassiveMode();

			//File management
			OutputStream outFile = new FileOutputStream(localFile.toFile());
			client.setFileType(FTP.BINARY_FILE_TYPE);

			//Loading file and set status
			status = client.retrieveFile(remoteFile.toFile().getPath().replace('\\', '/'), outFile);

			//Close streams and disconnect FTP
			outFile.close();
			client.logout();
			client.disconnect();

		}catch(IOException exception){
			status = false;
		}

		return status;
	}
}
