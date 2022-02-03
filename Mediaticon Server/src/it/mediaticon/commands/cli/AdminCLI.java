/* ****************************************************************
 * Author: Cristian Capraro
 * Made for MEDIATICON Project, in collaboration with:
 * 1) Giovanni Bellini
 * 2) Emanuele Trento
 * 3) Simone Destro
 *
 * This class provides the command line interface for guest users.
 * ****************************************************************/

package it.mediaticon.commands.cli;

import it.mediaticon.commands.CommandCLI;
import it.mediaticon.config.GlobalConfig;

import java.util.Scanner;

public class AdminCLI extends PrivilegedCLI{
	//CLI variables


	//CLI Commands

	@Override
	protected void commandSet(Scanner in) {
		//Guest commands + Privileged commands
		super.commandSet(in);
		super.command.remove("enable");
		//Administrator commands
		super.command.replace("privileged", () -> System.out.println("Admin mode")); //Shows privileged level
		super.command.put("addUser", () -> CommandCLI.addUser(in)); //Add a user password
		super.command.put("infoUser", CommandCLI::printUser); //Show user information
		super.command.put("shutdown", () -> System.exit(0)); //Shutdown -> Server exit
	}

	@Override
	protected void suggestCommand(String cmd) {
		super.suggestCommand(cmd);
	}

	//Constructor
	public AdminCLI(Scanner in){
		//Load command set
		commandSet(in);
		String line;

		while(true){
			System.out.print("\u001B[31mADM \u001B[35m" + GlobalConfig.hostname + "># \u001B[0m");
			line = in.nextLine();
			//Search command
			try{
				if(line.equals("end")) break; //Check end

				command.get(line).run();
			}catch(NullPointerException exception){
				suggestCommand(line);
			}

		}
	}



}