package it.mdrunner.cfg;

import it.mdrunner.ftp.FTPHandler;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class Plan implements Runnable {
	// ====== [Private fields] ======
	private final int PlanID;
	private final String AppName;
	private final int yearToScrape;
	private final LocalDateTime StartTime;
	private final boolean Repeatable;

	private LocalDateTime NextRepeat;

	//Next Repeat fields
	private ChronoUnit ChronosNext;
	private int AmountNext;

	//Fields for FTP Service
	private Path MidPath;

	// ====== [Public Fields] ======

	public LocalDateTime getStartTime() {
		return StartTime;
	}

	public String getAppName() {
		return AppName;
	}

	public Plan(int PlanID, String AppName, int yearToScrape, LocalDateTime StartTime, int NextRunLDT, ChronoUnit NextRunLDTUnit) {
		this.PlanID = PlanID;
		this.yearToScrape = yearToScrape;
		this.AppName = AppName;
		this.StartTime = StartTime;
		this.Repeatable = true;
		ChronosNext = NextRunLDTUnit;
		AmountNext = NextRunLDT;
		//Calculated NextRepeat
		NextRepeat = StartTime.plus(NextRunLDT, NextRunLDTUnit);
		MidPath = null;
	}

	public Plan(int PlanID, String AppName, int yearToScrape, LocalDateTime StartTime, int NextRunLDT, ChronoUnit NextRunLDTUnit, Path MidPath) {
		this.PlanID = PlanID;
		this.yearToScrape = yearToScrape;
		this.AppName = AppName;
		this.StartTime = StartTime;
		this.Repeatable = true;
		ChronosNext = NextRunLDTUnit;
		AmountNext = NextRunLDT;
		//Calculated NextRepeat
		NextRepeat = StartTime.plus(NextRunLDT, NextRunLDTUnit);
		this.MidPath = MidPath;
	}

	@Deprecated(forRemoval = true)
	public Plan(int PlanID, String AppName, int yearToScrape, LocalDateTime StartTime, LocalDateTime NextRepeat) {
		this.PlanID = PlanID;
		this.yearToScrape = yearToScrape;
		this.AppName = AppName;
		this.StartTime = StartTime;
		this.Repeatable = true;
		this.NextRepeat = NextRepeat;
	}

	public Plan(int PlanID, String AppName, int yearToScrape, LocalDateTime StartTime) {
		this.PlanID = PlanID;
		this.yearToScrape = yearToScrape;
		this.AppName = AppName;
		this.StartTime = StartTime;
		this.Repeatable = false;
	}

	@Override
	public String toString() {
		return "Plan ID = \033[31m" + PlanID + "\033[0m" + System.lineSeparator() + "App Name = \033[31m" + AppName + "\033[0m" + System.lineSeparator() +
				"Year param: \033[34m" + yearToScrape + "\033[0m" + System.lineSeparator() +
				"Next Run: \033[34m" + StartTime.format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")) + "\033[0m" + System.lineSeparator() +
				"Repeat: \033[33m" + (Repeatable ? "Yes" : "No") + "\033[0m" + System.lineSeparator() + "After Next Run: " + (NextRepeat != null ? NextRepeat : "Unset");
	}

	@Override
	public void run() {
		System.out.println("Timer@ Now executing: " + PlanID + " - " + AppName);
		ProcessBuilder process = AppLoader.pyReady(AppName, yearToScrape);
		process.redirectOutput(SharedConfig.LogFolder.resolve(AppName.toLowerCase() + ".txt").toFile());

		Thread thread = new Thread(() -> {
			try {
				//Before running, check and delete old files (if are already there)
				File outFile = new File(process.command().get(process.command().size() - 1));
				if (outFile.exists()) {
					boolean deleted = outFile.delete();
					if (!deleted) throw new IOException("Old File found - But can't delete.");
				}

				Process ps = process.start();
				PlanLoader.RunningPyPS.add(ps); //Add this PS to currently running list
				ps.waitFor(); //Wait ends
				if (ps.exitValue() != 0) {
					System.out.println("\033[31mTimer@ Execution: " + PlanID + " - " + AppName + System.lineSeparator() + "Ended with error.\033[0m"
							+ System.lineSeparator() + "Exit Code: " + ps.exitValue());

					//Remove this PS from currently running list
					PlanLoader.RunningPyPS.remove(ps);

					throw new IOException("Failed Scraper - Exit Code: " + ps.exitValue());
				} else {
					System.out.println("\033[32mTimer@ Execution: " + PlanID + " - " + AppName + System.lineSeparator() + "Ended correctly.\033[0m");

					//Remove this PS from currently running list
					PlanLoader.RunningPyPS.remove(ps);

					//Upload on FTP Off-Shore Server
					if (SharedConfig.FTPEnabled) {
						Path local = outFile.toPath();
						Path remote;

						if (MidPath != null) //MidPath is an override field
							remote = MidPath.resolve(local.getFileName());
						else
							remote = local.getFileName();

						System.out.println("\033[34mTimer @ FTP Service (Uploading)\033[0m");
						System.out.println("Local Path: " + local.toAbsolutePath() + System.lineSeparator() + "Remote Path: " + remote);

						if (FTPHandler.uploadFile(local, remote)) {
							System.out.println("\033[32mTimer @ FTP Service: UPLOAD OK\033[0m");
						} else {
							System.out.println("\033[31mTimer @ FTP Service: UPLOAD FAIL\033[0m");
							throw new IOException("Scraper OK. FTP Upload Failed.");
						}

					}

				}

			} catch (InterruptedException | IOException exc) {
				Path logFile = SharedConfig.LogFolder.resolve("FAIL_" + AppName.toLowerCase() + ".txt");
				try (BufferedWriter log = new BufferedWriter(new FileWriter(logFile.toFile(), true))) {
					log.write("[" + LocalDateTime.now() + "]--> " + "Error occurred with a scraper.");
					log.newLine();
					log.write(exc.getMessage());
					log.newLine();
					log.write("Plan ID = " + PlanID + "\t" + "Year = " + yearToScrape);
					log.newLine();
					log.write("Scraper initialized with the following arguments: ");
					log.newLine();
					log.write(process.command().toString());
					log.flush(); //Flush the stream
					for (int i = 0; i < 3; ++i) log.newLine(); //Separate errors
				} catch (IOException ignored) {
				}
			} finally {
				//Reschedule
				if (Repeatable) {
					PlanLoader.timerExe.remove(AppName);
					PlanLoader.timerExe.put(AppName, new Timer(AppName, false));

					System.out.println("\033[34mTimer@ Execution: " + PlanID + " - " + AppName);
					System.out.println("\033[34mNext run: " + NextRepeat + "\033[0m");
					System.out.println("\033[33mAfter Next run: " + NextRepeat.plus(AmountNext, ChronosNext) + "\033[0m");

					PlanLoader.timerExe.get(AppName).schedule(new TimerTask() {

						@Override
						public void run() {
							if (MidPath != null) //Check if MidPath is already set
								new Plan(PlanID, AppName, yearToScrape, NextRepeat, AmountNext, ChronosNext, MidPath).run();
							else //Init without MidPath (unnecessary)
								new Plan(PlanID, AppName, yearToScrape, NextRepeat, AmountNext, ChronosNext).run();
						}
					}, Date.from(NextRepeat.atZone(ZoneId.systemDefault()).toInstant()));
				}
			}
		});
		thread.start();
	}
}
