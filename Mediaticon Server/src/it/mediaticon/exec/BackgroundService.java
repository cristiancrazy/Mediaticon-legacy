package it.mediaticon.exec;

import it.mediaticon.config.GlobalConfig;

import java.io.IOException;
import java.lang.management.GarbageCollectorMXBean;
import java.net.InetAddress;
import java.time.Duration;
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
}
