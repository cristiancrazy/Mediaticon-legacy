/* *******************************************************
 * Author: Cristian Crazy
 * Project date: APR22
 * Project Website: https://mediaticon.000webhostapp.com
 * -------------------------------------------------------
 * Description:
 * The main purpose of this class is to initialize and
 * set parameters to run python executables.
 * *******************************************************/

package it.mdrunner.cfg;

import org.json.JSONArray;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.*;


public class AppLoader {
	// ====== [Private Fields & Methods] ======
	private static final ArrayList<File> pyApps = new ArrayList<>();
	private static final HashMap<String, List<String>> pyParams = new HashMap<>();
	private static final HashMap<String, List<String>> pyParamsOut = new HashMap<>();
	private static final ArrayList<ProcessBuilder> AppList = new ArrayList<>();

	/** Check if Python3 is available for this system **/
	private static boolean checkPython3(){
		ProcessBuilder checkPy = new ProcessBuilder("python3", "-V");

		//Task to perform on another thread.
		Callable<Integer> task = () -> {
			try{
				Process ps = checkPy.start();
				ps.waitFor();
				return(ps.exitValue());
			}catch (IOException exc){
				return -1; //Invalid return - Exception occurred
			}
		};

		ExecutorService exec = Executors.newSingleThreadExecutor();
		Future<Integer> result = exec.submit(task);

		try {
			if(result.get() == 0){
				exec.shutdown(); //Terminate Executor
				return true;
			}
		} catch (InterruptedException | ExecutionException e) {
			exec.shutdown(); //Terminate Executor
			return false; //Exception occurred;
		}

		return false; //Exception -> Different exit code.

	}

	/** Filter and load py apps. It fails if the folder does not contain python executables **/
	private static boolean getPyApps(){
		//Clear list if is already filled
		if(!pyApps.isEmpty()){
			pyApps.clear();
		}

		try{
			pyApps.addAll(Arrays.asList(Objects.requireNonNull(SharedConfig.AppFolder.toFile().listFiles(i -> i.getName().endsWith(".py")))));
			if(pyApps.isEmpty()) throw new NullPointerException(); //If any scraper was found.
		}catch (NullPointerException exc){
			return false;
		}

		return true;
	}

	/** Init python apps with the specified input arguments - Load from JSON **/
	private static boolean loadPyAppsArgument(){

		//Read app config file
		Path file = SharedConfig.AppConfigFile;
		StringBuilder fileBuffer = new StringBuilder();

		//Read config file and store data in the buffer
		try(FileReader in = new FileReader(file.toFile())){
			while(in.ready()) fileBuffer.append((char) in.read());
		}catch (IOException err){
			return false; //Exception occurred -> Can't continue.
		}

		JSONArray jsArray = new JSONArray(fileBuffer.toString());

		//Analyze data
		for(int i = 0; i < jsArray.length(); ++i){

			pyParams.put(jsArray.getJSONObject(i).getString("App Name"), jsArray.getJSONObject(i).getJSONArray("Params").toList().stream().map(k -> (String)k).toList());
		}

		//Replace @OUTPUT in the map
		for(String i : pyParams.keySet()){
			ArrayList<String> workP = new ArrayList<>(pyParams.get(i)); //Input
			ArrayList<String> workO = new ArrayList<>(); //Output

			for (String s : workP) {
				workO.add(s.replace("@OUTPUT", SharedConfig.OutputFolder.toAbsolutePath().toString()));
			}

			//Replace results
			pyParams.replace(i, workO);

		}

		return true;
	}


	/** Init python apps with the specified year **/
	private static void setPyAppsArgumentYear(int year){

		//Replace @YEAR in the map
		for(String i : pyParams.keySet()){
			ArrayList<String> workP = new ArrayList<>(pyParams.get(i)); //Input
			ArrayList<String> workO = new ArrayList<>(); //Output

			for (String s : workP) {
				workO.add(s.replace("@YEAR", Integer.toString(year)));
			}

			//Write results to new map
			if(pyParamsOut.containsKey(i))
				pyParamsOut.replace(i, workO);
			else
				pyParamsOut.put(i, workO);

		}

	}

	/** Create process builder for a specific python executable -> Set up python processes **/
	private static boolean initPyPS(String exeName){
		//Search
		for(File exe : pyApps){
			if(exe.getName().equals(exeName)&&pyParams.containsKey(exe.getName())){
				setupPyCommand(exe);
				return true;
			}
		}
		return false; //Scraper Not found | Error occurred
	}

	//Init process builder object for python executables
	private static void setupPyCommand(File exe) {
		ArrayList<String> command = new ArrayList<>();
		command.add("python3");
		command.add(exe.getAbsolutePath());
		command.addAll(pyParamsOut.get(exe.getName()));

		ProcessBuilder pb = new ProcessBuilder();
		pb.command(command);

		AppList.add(pb); //Add to ready app list
	}

	/** Create process builder for all valid python executable -> Set up python processes **/
	private static boolean initAllPyPS(){
		for(File exe : pyApps){
			if(pyParams.containsKey(exe.getName())){ //Check if configuration is provided
				setupPyCommand(exe);

			}else{
				System.out.println("Error with executable file: " + exe.getName());
				System.out.println("\033[31mParams configuration not found!\033[0m");
				return false;
			}
		}

		return true;
	}

	// ====== [ Public Fields & Methods] ======

	/** This method will pre-load python execution environment
	 *  It returns if there's a problem, for instance invalid apps and invalid arguments **/
	public static void initPython(){
		//Check Python 3
		if(checkPython3()){
			System.out.println("\033[32mPython 3 installation is valid.\033[0m");
		}else{
			System.out.println("\033[31mInvalid Python 3 installation. Please run dependecies installer for your system.\033[0m");
			System.exit(255); // Missing minimum requirements exit.
		}

		//Load py apps from folder, and parse params from the app. config (replace auto tags)
		if(getPyApps() && loadPyAppsArgument()){

			//Print valid py exec files
			System.out.println("\033[32mPython executable found:\033[0m");
			pyApps.forEach(System.out::println);

			System.out.println(PlanLoader.loadPlan());
			PlanLoader.start();

			PlanLoader.loadedPlanList.forEach(System.out::println);


		}else{
			System.out.println("\033[31mPython executable init error. Exiting!\033[0m");
			System.exit(253); // Content Missing or invalid exit.
		}
	}

	/** Ready to run (init single) -> Set year and get Process from the AppList **/
	public static ProcessBuilder pyReady(String appName, int year){
		setPyAppsArgumentYear(year);
		initPyPS(appName);
		return AppList.remove(AppList.size()-1);
	}

	/** ready to run (init all) -> Set year and insert all Process to the AppList **/
	@Deprecated(forRemoval = true)
	public static void pyReady(int year){
		setPyAppsArgumentYear(year);
		initAllPyPS();
	}

	/** Getter: Get loaded apps path **/
	public static ArrayList<File> getPyAppsList(){
		return pyApps;
	}

}