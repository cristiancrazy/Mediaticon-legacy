package it.mediaticon.exec;

import it.mediaticon.config.GlobalConfig;
import it.mediaticon.scraper.Loader;

import java.io.IOException;
import java.lang.management.GarbageCollectorMXBean;
import java.net.InetAddress;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.*;

public class BackgroundService{
    /* Instance variables */

    final static private ScheduledExecutorService serviceExec = Executors.newScheduledThreadPool(5);
    public static boolean networkUp;

    /* Tasks */

    //Memory Optimization
    final static private Runnable memoryOpt = () -> {
        Runtime.getRuntime().gc(); //Run garbage collector
    };

    //Check internet connection
    final static private Callable<Boolean> connVerifier = () -> {
        //Private variables
        boolean internet, ftp, smtp;

        //Test internet connection
        try{
            InetAddress test = InetAddress.getByName(GlobalConfig.defaultInternetAddress);
            internet = test.isReachable(GlobalConfig.defaultInternetTimeout);
        }catch (IOException err){
            internet = false;
        }

        //Check FTP Server status
        if(GlobalConfig.ftpAvailable){
            try{
                ftp = GlobalConfig.ftpAddress.isReachable(GlobalConfig.defaultInternetTimeout);
            }catch (IOException err){
                ftp = false;
            }
            GlobalConfig.ftpAvailable = ftp;
        }

        //Check SMTP Server status
        if(GlobalConfig.smtpAvailable){
            try{
                smtp = GlobalConfig.ftpAddress.isReachable(GlobalConfig.defaultInternetTimeout);
            }catch (IOException err){
                smtp = false;
            }
            GlobalConfig.ftpAvailable = smtp;
        }

        //Return general network info
        return internet;
    };

    static{
        //Task 1
        serviceExec.scheduleAtFixedRate(memoryOpt, 1,45, TimeUnit.MINUTES); //Repeat each 45 min

        //Task 2
        serviceExec.scheduleAtFixedRate(
                () -> {
                    try{
                        networkUp = connVerifier.call();
                    }catch (Exception ignored){ }
                }, 0, TimeUnit.SECONDS.convert(Duration.ofMinutes(2)), TimeUnit.SECONDS
        );

    }

    /* Start Scheduling scrapers */
    public static void enableScraper(TimeUnit unit, int start_delay, int repeat_delay){
        if(networkUp){ //Check internet connection
            List<String> scraper = Loader.getScraperAvailable();

            int index = 1;
            for(String name : scraper){
                System.out.println("\u001B[32m Impostazione scraper: " +
                        name + System.lineSeparator() + "Delay inziale:" + (start_delay*index) + unit.name() +
                        System.lineSeparator() + "Avviato ogni: " + repeat_delay + unit.name() + System.lineSeparator()
                        + "\u001B[0m");
                serviceExec.scheduleAtFixedRate(
                        () -> {
                            Loader.startScraper(name);
                        }, ((long) start_delay * index), repeat_delay, unit);

                index++;
            }
        }else{
            System.out.println("\u001B[31m"+"Connessione a Internet assente!"+"\u001B[0m\n");
        }

    }

    /** Manual internet connection verifier */
    public static boolean verifyConnNow(){
        try{
            networkUp = connVerifier.call();
        }catch(Exception ignored){ }

        return networkUp;
    }
}
