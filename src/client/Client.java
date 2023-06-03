package client ;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.sql.*;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class  Client implements Runnable {
    private static Socket client;
    private static ObjectInputStream in;
    private static ObjectOutputStream out;

    public static void main(String[] args) throws IOException {
        client = new Socket("localhost",6666);
        out = new ObjectOutputStream(client.getOutputStream());
        in = new ObjectInputStream(client.getInputStream());
        ShowMenu();

    }
    @Override
    public void run() {

    }
    public static void ShowMenu() throws IOException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("1.sign-up");
        System.out.println("2.sign-in");
        System.out.println("3.exit");
        int com = scanner.nextInt();
        if (com == 1){
            signUp();
        }
        else if (com == 2){

        }
        else if(com == 3){
            System.out.println("bye-bye");
            return;
        }
    }

    public static void signUp() throws IOException {
        Scanner scanner=new Scanner(System.in);
        ArrayList<String> newUserArgs = new ArrayList<String>();
        System.out.println("username:");
        newUserArgs.add(scanner.nextLine());
        System.out.println("firstname");
        newUserArgs.add(scanner.nextLine());
        System.out.println("lastname:");
        newUserArgs.add(scanner.nextLine());
        System.out.println("email");
        newUserArgs.add(scanner.nextLine());
        while (!emailValidity(newUserArgs.get(3))){
            System.out.println("invalid email format");
            newUserArgs.set(3,scanner.nextLine());
        }
        System.out.println("phone:");
        newUserArgs.add(scanner.nextLine());
        while(newUserArgs.get(3) == null && newUserArgs.get(4) == null){
            System.out.println("email and phone number is empty please choose to fill");
            System.out.println("1.email 2.phone 3.both");
            int choice = scanner.nextInt();
            if(choice==1){
                while (!emailValidity(newUserArgs.get(3))){
                    System.out.println("invalid email format try again");
                    newUserArgs.set(3,scanner.nextLine());
                }
                newUserArgs.set(3,scanner.nextLine());
            }
            else if(choice==2){
                newUserArgs.set(4,scanner.nextLine());
            }
            else if(choice==3){
                newUserArgs.set(3,scanner.nextLine());
                while (!emailValidity(newUserArgs.get(3))){
                    System.out.println("invalid email format try again");
                    newUserArgs.set(3,scanner.nextLine());
                }
                newUserArgs.set(4,scanner.nextLine());
            }

        }
        System.out.println("pass");
        newUserArgs.add(scanner.nextLine());
        while (!checkPass(newUserArgs.get(5))){
            System.out.println("invalid pass format");
            newUserArgs.set(5,scanner.nextLine());
        }
        System.out.println("pass again");
        newUserArgs.add(scanner.nextLine());
        while (!newUserArgs.get(5).equals(newUserArgs.get(6))){
            System.out.println("wrong pass");
            newUserArgs.set(6,scanner.nextLine());
        }
        System.out.println("country");
        newUserArgs.add(scanner.nextLine());
        System.out.println("birthdate");
        newUserArgs.add(scanner.nextLine());
        User newUser = new User(newUserArgs.get(0),newUserArgs.get(1),newUserArgs.get(2),newUserArgs.get(3),
                newUserArgs.get(4),newUserArgs.get(5),newUserArgs.get(7),newUserArgs.get(8));
        out.writeObject("sign-up");
        out.writeObject(newUser);
        try {
            if(((String) in.readObject()).equals("duplicate-id")){
                //todo handle
            }
            else if(((String) in.readObject()).equals("duplicate-email")){
                //todo handle
            }
            else if(((String) in.readObject()).equals("duplicate-number")){
                //todo handle
            }
            else if(((String) in.readObject()).equals("success")){
                //todo handle
            }
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
    public static boolean emailValidity(String email){
        String regex = "^[A-Za-z0-9+_.-]+@(.+)$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    public static boolean checkPass(String pass){
        boolean isOkay=true;
        if(pass.length() < 8)
            isOkay=false;
        if(!pass.matches(".*[A-Z].*") || !pass.matches(".*[a-z]*."))
            isOkay=false;
        return isOkay;
    }

}

class SignInClient implements Runnable{
    @Override
    public void run() {

    }
}