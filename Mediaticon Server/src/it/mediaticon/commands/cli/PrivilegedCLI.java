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

public class PrivilegedCLI extends GuestCLI{
	//CLI variables


	//CLI commands
	@Override
	protected void commandSet(Scanner in) {
		//Guest commands
		super.commandSet(in);
		//Privileged commands
		super.command.put("privileged", () -> System.out.println("Privileged mode"));
		super.command.put("reload", () -> CommandCLI.reload(in));
		super.command.put("save", () -> CommandCLI.save(in));
	}

	@Override
	protected void suggestCommand(String cmd) {
		super.suggestCommand(cmd);
	}

	//Constructor
	protected PrivilegedCLI() { }

	public PrivilegedCLI(Scanner in){
		//Load command set
		commandSet(in);
		String line;

		while(true){
			System.out.print("\u001B[35m" + hostname + "># \u001B[0m");
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