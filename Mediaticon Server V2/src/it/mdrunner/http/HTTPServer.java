/* *******************************************************
 * Author: Cristian Crazy
 * Project date: MAY22
 * Project Website: https://mediaticon.000webhostapp.com
 * -------------------------------------------------------
 * Description:
 * This class is made for enabling a web interface to
 * view server statistics
 * *******************************************************/

package it.mdrunner.http;

import com.sun.net.httpserver.BasicAuthenticator;
import com.sun.net.httpserver.HttpServer;
import it.mdrunner.cfg.PlanLoader;
import it.mdrunner.cfg.SharedConfig;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class HTTPServer {
	// ============ PRIVATE ============
	private static HttpServer WebServer;

	private static String getDefaultPage(){
		StringBuilder sb = new StringBuilder();
		Path toLoad = SharedConfig.ConfigFile.getParent().resolve("default.html");
		try(BufferedReader reader = Files.newBufferedReader(toLoad)){
			String line;
			while((line = reader.readLine()) != null) sb.append(line);
		}catch (IOException ioe){
			System.out.println("Something goes wrong with the HTML default file.");
		}

		if(sb.isEmpty()){
			return "<!DOCTYPE html><html><head><title>Error occurred</title></head><body><div>Error occurred with Web service.</div></body></html>";
		}
		return sb.toString();

	}

	/** This method will init Web server before starting it **/
	private static boolean init(String address, int port){
		try{

			//Setup authenticator rules
			BasicAuthenticator authenticator = new BasicAuthenticator("/") {
				@Override
				public boolean checkCredentials(String username, String password) {
					return (username.equals(new String(SharedConfig.DefaultWebUser))&&password.equals(new String(SharedConfig.DefaultWebPass)));
				}
			};

			//Setup webserver

			WebServer = HttpServer.create(new InetSocketAddress(address, port), 0);
			WebServer.createContext("/", (handler) -> {
				handler.sendResponseHeaders(200, 0);
				try(OutputStreamWriter osw = new OutputStreamWriter(handler.getResponseBody())){
					osw.write(getDefaultPage().split("</head>")[0]);
					osw.write(String.format(getDefaultPage().split("</head>")[1], SharedConfig.ServerUptime.format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")), LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")), PlanLoader.RunningPyPS.size(), SharedConfig.FinishedTasks, SharedConfig.FinishedTasksWithError));
				}catch (Exception ioe){
					System.out.println("Error with web server occurred...");
					ioe.printStackTrace();
				}
			}).setAuthenticator(authenticator);


		}catch(IOException ioe){ //Something goes wrong
			return false;
		}
		return true;
	}

	// ============ PUBLIC ============
	public static void StartHTTPServer(String address, int port){
		if(init(address, port)){
			WebServer.start();
			System.out.println("Web Server Started successfully.");
		}else{
			System.out.println("Web Server Failed to start.");
		}
	}
}
