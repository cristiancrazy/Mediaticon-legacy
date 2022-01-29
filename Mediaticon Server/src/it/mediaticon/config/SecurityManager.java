/* ****************************************************************
 * Author: Cristian Capraro
 * Made for MEDIATICON Project, in collaboration with:
 * 1) Giovanni Bellini
 * 2) Emanuele Trento
 * 3) Simone Destro
 *
 * This class has been made to load configuration data from
 * external file or export it.
 * ****************************************************************/


package it.mediaticon.config;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Scanner;

public class SecurityManager {

    private enum userType{
        PRIVILEGED, ADMINISTRATOR
    }

    /** Password encoder - this method convert plain text to encoded text**/
    public static String encodePassword(String toBeEncoded){
        try{
            //Encoding password
            MessageDigest msgd = MessageDigest.getInstance("SHA-256");
            msgd.update(toBeEncoded.getBytes(StandardCharsets.UTF_8));
            return new String(msgd.digest()); //Return encoded string
        }catch(NoSuchAlgorithmException ignored){ }
        return null; //On error
    }

    /** Load security information from config external file **/
    public static boolean loadSecurity(){
        String line, auth, type;
        boolean status = true;
        //Reading file
        try(BufferedReader in = new BufferedReader(new FileReader(GlobalConfig.securityConf.toFile()))){
            while((line = in.readLine())!=null){
                //Check
                if(line.startsWith("$AUTH = ")){
                    auth = line.substring(8, line.indexOf(" $TYPE")); //Password
                    type = line.substring(line.indexOf("$TYPE =")+8); //Account type

                    //Divide by account type
                    if(type.equals("ADM")){
                        GlobalConfig.adminPassword.add(auth);
                    }else{
                        GlobalConfig.privilegedPassword.add(auth);
                    }
                }

            }

        }catch(IndexOutOfBoundsException | IOException exception){
            status = false;
        }

        return status;
    }

    /** Adding new password wizard **/
    public static void addUser(Scanner in){
        String psw1, psw2; //Password string

        //User input
        System.out.println("[ADDING NEW USER]");
        try{
            psw1 = new String(System.console().readPassword("Insert password: "));
            psw2 = new String(System.console().readPassword("Confirm password: "));

            if(psw1.equals(psw2)&&(!psw1.isEmpty())){
                //Choose password type
                System.out.print("Password type? (PRV/ADM): ");

                String answer = in.nextLine();

                if(answer.matches("PRV|prv")){
                    if(updateUser(psw1, userType.PRIVILEGED)){
                        if(loadSecurity()) //Reloading
                            System.out.println("Password set correctly.");
                        else
                            System.out.println("Security Error.");
                    }


                }else if(answer.matches("ADM|adm")){
                    if(updateUser(psw1, userType.ADMINISTRATOR)){
                        if(loadSecurity()) //Reloading
                            System.out.println("Password set correctly.");
                        else
                            System.out.println("Security Error.");
                    }

                }else{ //Fail #2
                    System.out.println("Mode unrecognized. Aborting");
                }
            }else{ //Fail #1
                System.out.println("Password confirmation failed.");
            }

        }catch (NullPointerException exception){
            //Text message to user

            System.out.println(
                    "Server error;" + System.lineSeparator() +
                            "Remember, if you are in a simulation, this is normal." +
                            System.lineSeparator() +
                            "You should use a real console to run this portion of code."
            );
        }
    }

    /** Add user on the security configuration file **/
    private static boolean updateUser(String password, userType type){
        boolean status;
        //Append on a file
        try(BufferedWriter out = new BufferedWriter(new FileWriter(GlobalConfig.securityConf.toFile(), true))){
            out.write("$AUTH = " +
                    encodePassword(password) +
                    " $TYPE = " + ((type.equals(userType.PRIVILEGED))? "PRV" : "ADM")
            );
            out.newLine();
            status = true;
        }catch(IOException exception){
            status = false;
        }


        return status;
    }
}