/* ****************************************************************
 * Author: Cristian Capraro
 * Made for MEDIATICON Project, in collaboration with:
 * 1) Giovanni Bellini
 * 2) Emanuele Trento
 * 3) Simone Destro
 *
 * This class is used to manage the 'Scraper directory' and all
 * the scrapers program. In this class there are methods to start
 * and kill processes.
 ******************************************************************/

package it.mediaticon.config;

import it.mediaticon.exec.MainClass;

import java.io.*;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

public class ScraperManager {
	//Private fields
	private static List<ProcessBuilder> scraperProcessList = new ArrayList<>();
	private static List<File> scraperAvailable = new ArrayList<>(); //This list contains all scraper files

	//Private Methods

	/** This initialize and check scraper directory **/
	private static boolean initDir(){
		boolean status = true; //Method status

		scraperAvailable.clear(); //Reset list

		File dir = GlobalConfig.scraperDir.toFile();
		//Check directory
		if(dir.exists()){
			File[] scraper = dir.listFiles();

			if((scraper!=null)&&(scraper.length > 0)){
				//Check file type

				/* Python files */
				scraperAvailable.addAll(Arrays.stream(scraper).filter(i -> i.getName().endsWith(".py")).collect(Collectors.toList()));

				/* Java and Java bytecode files */
				scraperAvailable.addAll(Arrays.stream(scraper).filter(i -> i.getName().endsWith(".java")).collect(Collectors.toList()));
				scraperAvailable.addAll(Arrays.stream(scraper).filter(i -> i.getName().endsWith(".class")).collect(Collectors.toList()));
				scraperAvailable.addAll(Arrays.stream(scraper).filter(i -> i.getName().endsWith(".jar")).collect(Collectors.toList()));

				if(scraperAvailable.isEmpty()) status = false; //If no elements are contained in the list

			}else{
				status = false;
			}

		}else{
			status = false;
		}

		return status;
	}

	/** This method will add information - to start the scraper with the appropriate commands **/
	private static boolean initScraper(int number) throws IndexOutOfBoundsException{
		boolean status = false; //Method status

		//Find scraper in the list
		File scraper = scraperAvailable.get(number);

		if(scraper!=null){
			//Check file type and execute the appropriate command to start process
			String scraperName = scraper.getName();

			if(scraperName.endsWith(".py")){ //Python file
				ProcessBuilder pb = new ProcessBuilder("python", scraper.getAbsolutePath());
				//pb.redirectOutput(Path.of("scraper", "out.txt").toFile());
				scraperProcessList.add(pb);
				status = true;
			}

			if(scraperName.endsWith(".java")){ //Java file
				ProcessBuilder pb = new ProcessBuilder("java", scraper.getAbsolutePath());
				pb.redirectOutput(Path.of("scraper", "out2.txt").toFile());
				scraperProcessList.add(pb);
				status = true;
			}

			if(scraperName.endsWith(".jar")){ //Jar compressed file
				ProcessBuilder pb = new ProcessBuilder("java", "--jar", scraper.getAbsolutePath());
				scraperProcessList.add(pb);
				status = true;
			}
			//TODO: CORRECTION NEEDED
			/*if(scraperName.endsWith(".class")){ //Java bytecode file (Java / scala ...)
				ProcessBuilder pb = new ProcessBuilder("java "+scraperName.substring(0, scraperName.indexOf(".class")));
				scraperProcessList.add(pb);
				status = true;
			}*/

		}
		return status;
	}

	/** This method will start scraper process **/
	private static boolean startScraper(int number){
		Future<Boolean> status = MainClass.executor.submit(
				() -> {
					ProcessBuilder pb = scraperProcessList.get(number);
					try{
						Process process = pb.start();
						System.out.println(process);
						BufferedInputStream is = new BufferedInputStream(process.getInputStream());

						process.waitFor();
						return true;
					}catch (InterruptedException | IOException ignored){
						return false;
					}
				}
		);

		//Wait finishing the task submitted and return results if possible
		try{
			return status.get();
		}catch (ExecutionException exc){
			System.out.println("Exec exception" +exc.getMessage());
			return false;
		}catch (InterruptedException exc){
			System.out.println("Exception" + exc.getMessage());
			return false;
		}
	}

	//TODO: IMPLEMENT THIS
	/** This method will start all scraper processes **/
	private static boolean startScraper(){
		return false;
	}


	//Public Methods

	/** This method will load the scraper directory and to set up
	 * scraper's information required to start these programs.*/
	public static boolean loadScrapers(){
		boolean status = true; //Method status

		//init file scraper directory

		if((status = initDir())){
			//Add correct parameters to start scrapers
			for(int i = 0; i < scraperAvailable.size(); ++i){
				status = initScraper(i);
				if(!status) return false; //Init scraper failed
			}
		}

		return status;
	}

}