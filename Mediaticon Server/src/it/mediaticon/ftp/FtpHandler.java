/* ****************************************************************
 * Author: Cristian Capraro
 * Made for MEDIATICON Project, in collaboration with:
 * 1) Giovanni Bellini
 * 2) Emanuele Trento
 * 3) Simone Destro
 *
 * The purpose of this class is to provide connectivity, and to
 * make possible to download or upload data via FTP Protocol.
 ******************************************************************/

package it.mediaticon.ftp;

import java.io.*;
import java.net.InetAddress;
import java.nio.file.Path;

import it.mediaticon.config.GlobalConfig;

//External dependencies

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;


public class FtpHandler {
	//FTP Settings fields
	private static InetAddress ftpAddress = GlobalConfig.ftpAddress;
	private static int ftpPort = GlobalConfig.ftpPort;
	private static String ftpUsername = GlobalConfig.ftpUsername;
	private static String ftpPassword = GlobalConfig.ftpPassword;

	/** Use this method to se ftp settings - overriding global config **/
	public static void setFtp(InetAddress address, int port, String user, String pass){
		ftpAddress = address;

		if((port >= 0)&&(port < 65535)){
			ftpPort = port;
		}else{
			ftpPort = 21; //Set default
		}

		ftpUsername = user;
		ftpPassword = pass;

	}

	/** Upload file via an ftp connection
	 *  This method returns a 'true' boolean value if operation
	 *  is completed successfully **/
	public static boolean uploadFile(Path localFile, Path remoteFile){
		boolean status = true;

		FTPClient client = new FTPClient();
		try{

			//Connection to FTP
			client.connect(ftpAddress, ftpPort);
			client.login(ftpUsername, ftpPassword);

			client.enterLocalPassiveMode();

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
			status = true;
		}

		return status;
	}


	/** Download file via an ftp connection
	 * This method returns a 'true' boolean value
	 * if operation is completed successfully **/
	public static boolean downloadFile(Path remoteFile, Path localFile){
		boolean status = true;

		FTPClient client = new FTPClient();
		try{

			//Connection to FTP
			client.connect(ftpAddress, ftpPort);
			client.login(ftpUsername, ftpPassword);

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