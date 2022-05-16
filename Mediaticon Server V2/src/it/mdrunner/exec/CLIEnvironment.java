/* *******************************************************
 * Author: Cristian Crazy
 * Project date: MAY22
 * Project Website: https://mediaticon.000webhostapp.com
 * -------------------------------------------------------
 * Description:
 * The main purpose of this class is to offer a minimal
 * CLI environment to manage the application (standalone)
 * *******************************************************/

package it.mdrunner.exec;

import java.util.HashMap;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class CLIEnvironment{
	// ====== CLASS INIT ======
	private static final HashMap<String, Runnable> CommandMap = new HashMap<>();
	private static final Scanner UserAction = new Scanner(System.in);


	static{
		CommandMap.put("help", () -> CLICommands.CLIHelp(CommandMap));
		CommandMap.put("?", () -> CLICommands.CLIHelp(CommandMap));
		CommandMap.put("ls", CLICommands::showAllPlan);
		CommandMap.put("show config", CLICommands::showConfig);
		CommandMap.put("show plans", () -> CLICommands.showPlan(UserAction));
		CommandMap.put("ftp test", () -> CLICommands.testFTP(UserAction));
		CommandMap.put("clear", CLICommands::clearScreen);
		CommandMap.put("cls", CLICommands::clearScreen);
		CommandMap.put("shutdown", CLICommands::shutdown);
		CommandMap.put("quit", CLICommands::shutdown);

	}

	// ====== PRIVATE FIELDS =====


	/** This method will init the environment **/
	private static void initCLI(){
		System.out.println(System.lineSeparator() + "MD(TM) Executor Service - [MAY22]" + System.lineSeparator() + "Minimal Integrated CLI Environment");

		while(true){
			System.out.print("Runner # ");
			try{
				parseCMD(UserAction.nextLine());
			}catch (NoSuchElementException ignored){ }
		}
	}

	/** Search command and execute associated action **/
	private static void parseCMD(String in){
		CommandMap.getOrDefault(in, () -> System.out.println("\033[31mInvalid command. Key \"help\" to show command list.\033[0m")).run();
	}

	// ======= PUBLIC FIELDS ========
	public static final Thread CLIThread = new Thread(CLIEnvironment::initCLI);
}
