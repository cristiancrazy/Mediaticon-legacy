/* *******************************************************
 * Author: Cristian Crazy
 * Project date: MAY22
 * Project Website: https://mediaticon.000webhostapp.com
 * -------------------------------------------------------
 * Description:
 * The main purpose of this class is to generate
 * timetables/plan to run scrapers.
 * *******************************************************/

package it.mdrunner.exec;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;


public class PlanGenerator {
	// ======= PRIVATE =======

	// ====== PUBLIC =======

	/**
	 * Generate automatically a plan for specific application
	 **/
	public static void generatePlan(String[] params) {
		String AppName = "", TimeStart = "", DateStart = "", MidPath = "";
		int Year = 0, YearSpan = 0, TimeSpan = 0; //YearSpan: increase year range - TimeSpan: time range between two execution

		try {

			AppName = params[1];
			Year = Integer.parseInt(params[2]);
			YearSpan = Integer.parseInt(params[3]);
			TimeSpan = Integer.parseInt(params[4]);
			MidPath = params[5];


		} catch (NumberFormatException | ArrayIndexOutOfBoundsException exc) {
			System.out.println("\033[31mParams error. Exiting\033[0m");
			System.out.println("\033[33mExample: ScraperExecutor.jar AppName Year Year_Span(number) Date_Span(number) Remote_Path\033[0m");
		}

		//Write on file
		LocalDateTime refer = LocalDateTime.now().plus(5, ChronoUnit.MINUTES);
		try (BufferedWriter out = new BufferedWriter(new FileWriter("plan.json", false))) {
			out.write("[");
			out.newLine();

			for (int ID = 1; ID <= YearSpan; ++ID) {
				out.write(String.format(
						"""
									{
									  "ID": %d,
									  "App Name": "%s",
									  "Year": %d,
									  "Start Time": "%s",
									  "Start Date": "%s",
									  "Repeat Each": "No",
									  "MidPath": "%s"
									}%s
								""", ID, AppName, (Year + ID - 1), refer.format(DateTimeFormatter.ofPattern("HH:mm")), refer.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")), MidPath, ((ID == YearSpan) ? "\n]" : ",")
				));
				refer = refer.plus(TimeSpan, ChronoUnit.MINUTES);
			}

			System.out.println("\033[32mOK. Exiting\033[0m");
		} catch (IOException exc) {
			System.out.println("\033[31mIO File Error\033[0m");
		}
	}
}