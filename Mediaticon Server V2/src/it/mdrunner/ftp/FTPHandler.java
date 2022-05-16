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
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


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

		} catch (IOException exception) {
			System.out.println(exception.getMessage());
			status = false;
		}

		return status;
	}

	/**
	 * Generating a specific file and upload on the FTP server bound with settings
	 **/
	public static void FTPTest(String remotePath) {
		//Preliminary Check
		if (!SharedConfig.FTPEnabled) {
			System.out.println("Please enable FTP from configuration file.");
			return;
		}

		//Generate file
		String filepath = "." + File.separator + "ftpExchange.test";
		File test = new File(filepath);
		remotePath += test.getName();

		//Writing data
		try (BufferedWriter bwr = new BufferedWriter(new FileWriter(test))) {
			bwr.write("--[BEGIN REPORT]--");
			bwr.newLine();
			bwr.write("[MD SERVER - V2 MAY-22]");
			bwr.newLine();
			bwr.write("[This file is generated automatically and is only for testing purposes]");
			bwr.newLine();
			bwr.write("Details:");
			bwr.newLine();
			bwr.write(String.format("Date/Time: %s/%s", LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MMM-yyyy")), LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm"))));
			bwr.newLine();
			bwr.write(String.format("Uploading on: %s:%d", SharedConfig.FTPServer, SharedConfig.FTPServerPort));
			bwr.newLine();
			bwr.write("--[END REPORT]--");

		} catch (IOException exception) {
			System.out.println("\033[31mIO Error with test file.\033[0m");
		}

		//Try to auth
		System.out.print("FTP Authentication result: ");
		FTPClient client = new FTPClient();
		try {
			client.connect(SharedConfig.FTPServer, SharedConfig.FTPServerPort);
			if (client.login(SharedConfig.FTPUser, SharedConfig.FTPPassword)) {
				System.out.println("\033[32mPASS\033[0m");
				System.out.println(client.getStatus());
			} else {
				System.out.println("\033[31mFAILED\033[0m");
				System.out.println(client.getStatus());
			}
			client.disconnect();
		} catch (IOException e) {
			System.out.println("\033[31mFAILED\033[0m");
		}

		//Try to upload on remote server
		System.out.print("FTP uploading test result: ");
		if (uploadFile(Path.of(filepath), Path.of(remotePath))) {
			System.out.println("\033[32mPASS\033[0m");
		} else {
			System.out.println("\033[31mFAILED\033[0m");
		}

		//Try to download from remote server - Downloading test
		System.out.print("FTP downloading test result: ");
		String filepath2 = filepath + "d";
		if (downloadFile(Path.of(remotePath), Path.of(filepath2))) {
			System.out.println("\033[32mPASS\033[0m");
		} else {
			System.out.println("\033[31mFAILED\033[0m");
		}

		//Check file integrity
		try {
			if (Files.mismatch(Path.of(filepath), Path.of(filepath2)) == -1L) {
				System.out.println("\033[32mData integrity comparison passed.\033[0m");
				for (String i : Files.readAllLines(Path.of(filepath2), StandardCharsets.UTF_8)) {
					System.out.println(i);
				}
			} else throw new IOException();
		} catch (IOException e) {
			System.out.println("\033[31mData integrity comparison failed.\033[0m");
		}

		if ((!test.delete()) || (!Path.of(filepath2).toFile().delete()))
			System.out.println("\033[31mError with file deletion\033[0m");


	}

}
