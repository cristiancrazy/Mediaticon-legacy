/* *******************************************************
 * Author: Cristian Crazy
 * Project date: APR22
 * Project Website: https://mediaticon.000webhostapp.com
 * -------------------------------------------------------
 * Description:
 * This class is made for loading and exporting data
 * through the config file (JSON formatted).
 * NB: This class will use a JSON Parser Library, remember
 * JSON is a comfortable file format, you must trust me.
 * *******************************************************/

package it.mdrunner.cfg;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileReader;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.function.Predicate;

public class ConfigLoader {

	// =======[Private Methods]=======
	private static final Predicate<Integer> checkServicePort = i -> (i == 80) || (i == 21) || (i == 20) || ((i > 1023)&&(i < 65536));

	private static final Predicate<String> checkFolder = s -> (Path.of(s).toFile().exists()&&Path.of(s).toFile().isDirectory());

	private static final Predicate<String> checkFile = s -> (Path.of(s).toFile().exists()&&Path.of(s).toFile().isFile());

	@SuppressWarnings("ResultOfMethodCallIgnored")
	private static final Predicate<String> checkAddress = s -> {
		try {
			InetAddress.getByName(s);
			return true;
		} catch (UnknownHostException e) {
			return false;
		}
	};

	// =======[Public Methods]=======

	/** It loads from the configuration file the file/folder path, service configuration
	 * actions and other things, to set up the app. **/
	public static boolean loadConfigFile(){
		//Get the config file from "shared config class"
		Path file = SharedConfig.ConfigFile;
		StringBuilder fileBuffer = new StringBuilder();

		//Read config file and store data in the buffer
		try(FileReader in = new FileReader(file.toFile())){
			while(in.ready()) fileBuffer.append((char) in.read());
		}catch (IOException err){
			return false; //Exception occurred -> Can't continue.
		}

		//Parse JSON and get all fields
		JSONObject jsonParser = new JSONObject(fileBuffer.toString());

		//Buffer variables to store temporary data.
		String bufferS;
		int bufferI;

		try{
			//Getting File paths
			if(checkFile.test(bufferS = jsonParser.getString("App Config File"))){
				SharedConfig.AppConfigFile = Path.of(bufferS);
			}else throw new JSONException("");

			if(checkFile.test(bufferS = jsonParser.getString("Time Plan File"))){
				SharedConfig.TimePlanFile = Path.of(bufferS);
			}else throw new JSONException("");

			//Getting Folder paths
			if(checkFolder.test(bufferS = jsonParser.getString("App Folder"))){
				SharedConfig.AppFolder = Path.of(bufferS);
			}else throw new JSONException("");

			if(checkFolder.test(bufferS = jsonParser.getString("Output Folder"))){
				SharedConfig.OutputFolder = Path.of(bufferS);
			}else throw new JSONException("");

			if(checkFolder.test(bufferS = jsonParser.getString("Log Folder"))){
				SharedConfig.LogFolder = Path.of(bufferS);
			}else throw new JSONException("");

			//Other fields - Addresses, Int, Boolean and Strings
			SharedConfig.FTPEnabled = jsonParser.getBoolean("FTP Service");

			if(SharedConfig.FTPEnabled){

				if(checkServicePort.test(bufferI = jsonParser.optInt("FTP Port", 21))){
					SharedConfig.FTPServerPort = bufferI;
				}else throw new JSONException("");

				SharedConfig.FTPUser = jsonParser.optString("FTP Username", "anonymous");

				SharedConfig.FTPPassword = jsonParser.optString("FTP Password", "");

				if(checkAddress.test(bufferS = jsonParser.getString("FTP Server"))){
					SharedConfig.FTPServer = InetAddress.getByName(bufferS);
				}else throw new JSONException("");

			}

			//Webservices
			SharedConfig.WEBEnabled = jsonParser.optBoolean("WEB Service", false);

			if(SharedConfig.WEBEnabled){

				SharedConfig.WEBServer = jsonParser.getString("WEB Server");

				if(checkServicePort.test(bufferI = jsonParser.optInt("WEB Port", 80))){
					SharedConfig.WEBServerPort = bufferI;
				}else throw new JSONException("");

				SharedConfig.DefaultWebUser = jsonParser.getString("WEB Username").getBytes(StandardCharsets.UTF_8);
				SharedConfig.DefaultWebPass = jsonParser.getString("WEB Password").getBytes(StandardCharsets.UTF_8);

			}

		}catch (UnknownHostException |org.json.JSONException exc){
			return false;
		}

		//Otherwise, if it all is correct:
		return true;
	}

}