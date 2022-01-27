package it.mediaticon.exec;



import java.util.Scanner;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import it.mediaticon.commands.cli.GuestCLI;


public class MainClass {
	public static void main(String[] args){
		StartupHandler.startup();
		ExecutorService executor = Executors.newCachedThreadPool();
		executor.submit(() -> new GuestCLI(new Scanner(System.in)));
		executor.shutdown();

	}
}
