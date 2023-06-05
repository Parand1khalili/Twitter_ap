package client ;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Date;
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
            signIn();
        }
        else if(com == 3){
            System.out.println("bye-bye");
            out.writeObject("exit");
            return;
        }
        else {
            System.out.println("please read your options again");
            ShowMenu();
        }
    }

    public static void signUp() {
        Scanner scanner=new Scanner(System.in);
        ArrayList<String> newUserArgs = new ArrayList<String>();
        System.out.println("username:");
        newUserArgs.add(scanner.nextLine());
        while (newUserArgs.get(0) == null){
            System.out.println("please enter username!!!");
            newUserArgs.set(0,scanner.nextLine());
        }
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
        try {
            out.writeObject("sign-up");
            Thread.sleep(500);
            out.writeObject(newUser);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
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
                login(newUser.getId());
            }
        } catch (ClassNotFoundException | IOException e) {
            throw new RuntimeException(e);
        }
    }
    public static void signIn (){
        Scanner scanner = new Scanner(System.in);
        ArrayList<String> theUserArgs = new ArrayList<>();
        System.out.println("username:");
        theUserArgs.add(scanner.nextLine());
        while (theUserArgs.get(0) == null){
            System.out.println("please enter username!!!");
            theUserArgs.set(0,scanner.nextLine());
        }
        System.out.println("password:");
        theUserArgs.add(scanner.nextLine());
        while (theUserArgs.get(1) == null){
            System.out.println("please enter your password!!!");
            theUserArgs.set(1,scanner.nextLine());
        }
        User theUser = new User(theUserArgs.get(0),theUserArgs.get(1));
        try {
            out.writeObject("sign-in");
            Thread.sleep(500);
            out.writeObject(theUser);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
        try {
            if(((String) in.readObject()).equals("not-found")){
                System.out.println("user not found try again");
                signIn();
            }
            else if(((String) in.readObject()).equals("wrong-pass")){
                System.out.println("wrong password try again");
                signIn();
            }
            else if(((String) in.readObject()).equals("success")){
                login(theUser.getId());
            }
        } catch (ClassNotFoundException | IOException e) {
            throw new RuntimeException(e);
        }
    }
    public static void login(String id){
        Scanner scanner = new Scanner(System.in);
        User loggedUser;
        try {
            out.writeObject("get-user");
            Thread.sleep(500);
            out.writeObject(id);
            Thread.sleep(500);
            loggedUser = (User) in.readObject();
        } catch (IOException | InterruptedException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        System.out.println("welcome "  + loggedUser.getFirstName() +" !");
        System.out.print("1.profile\n2.time line\n3.new tweet\n4.search\n5.logout\n");
        int choice = scanner.nextInt();
        if(choice == 1){
            ownProfile(loggedUser);
        }
        else if (choice == 2){
            timeLine(loggedUser);
        }
        else if (choice == 3){
            newTweet(loggedUser);
        }
        else if (choice == 4){
            search(loggedUser);
        }
        else if (choice == 5){
            try {
                ShowMenu();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
    public static void ownProfile (User loggedUser){
        Scanner scanner = new Scanner(System.in);
        System.out.println(loggedUser.getFirstName() + " " + loggedUser.getLastName());
        System.out.print("1.edit profile\n2.edit header\n3.edit bio\n4.edit web\n" +
                "5.edit location\n6.back\n7.show profile\n");
        int choice = scanner.nextInt();
        if(choice == 1){
            editProfile(loggedUser);
        }
        else if(choice == 2){
            editHeader(loggedUser);
        }
        else if(choice == 3){
            editBio(loggedUser);
        }
        else if(choice == 4){
            editWeb(loggedUser);
        }
        else if(choice == 5){
            editLocation(loggedUser);
        }
        else if(choice == 6){
            login(loggedUser.getId());
        }
        else if(choice == 7){
            showProfile(loggedUser);
        }

    }
    public static void editProfile(User loggedUser){
        Scanner scanner = new Scanner(System.in);
        String newProfPicPath;
        System.out.println("please enter the path of the new profile picture");
        newProfPicPath = scanner.nextLine();
        while (newProfPicPath==null){
            System.out.println("please enter a valid path!");
            newProfPicPath = scanner.nextLine();
        }
        // check size of picture
        try {
            out.writeObject("edit-profile");
            Thread.sleep(50);
            out.writeObject(loggedUser);
            Thread.sleep(50);
            out.writeObject(newProfPicPath);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }

    }
    public static void editHeader(User loggedUser){
        Scanner scanner = new Scanner(System.in);
        String newHeaderPicture;
        System.out.println("please enter the path of the new header picture");
        newHeaderPicture = scanner.nextLine();
        while (newHeaderPicture==null){
            System.out.println("please enter a valid path!");
            newHeaderPicture = scanner.nextLine();
        }
        // check size of picture
        try {
            out.writeObject("edit-header");
            Thread.sleep(50);
            out.writeObject(loggedUser);
            Thread.sleep(50);
            out.writeObject(newHeaderPicture);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
    public static void editBio(User loggedUser){
        Scanner scanner = new Scanner(System.in);
        String newBio;
        System.out.println("please enter the new bio");
        newBio = scanner.nextLine();
        while (newBio==null){
            System.out.println("please enter a valid bio!");
            newBio = scanner.nextLine();
        }
        while (newBio.length() > 160){
            System.out.println("your text should be less than 160 characters!");
            newBio = scanner.nextLine();
        }
        try {
            out.writeObject("edit-bio");
            Thread.sleep(50);
            out.writeObject(loggedUser);
            Thread.sleep(50);
            out.writeObject(newBio);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
    public static void editWeb(User loggedUser){
        Scanner scanner = new Scanner(System.in);
        String newWeb;
        System.out.println("please enter the new web");
        newWeb = scanner.nextLine();
        while (newWeb==null){
            System.out.println("please enter a valid web!");
            newWeb = scanner.nextLine();
        }
        try {
            out.writeObject("edit-web");
            Thread.sleep(50);
            out.writeObject(loggedUser);
            Thread.sleep(50);
            out.writeObject(newWeb);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
    public static void editLocation(User loggedUser){
        Scanner scanner = new Scanner(System.in);
        String newLoc;
        System.out.println("please enter the new location");
        newLoc = scanner.nextLine();
        while (newLoc==null){
            System.out.println("please enter a valid text!");
            newLoc = scanner.nextLine();
        }
        try {
            out.writeObject("edit-location");
            Thread.sleep(50);
            out.writeObject(loggedUser);
            Thread.sleep(50);
            out.writeObject(newLoc);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
    public static void showProfile(User loggedUser){
        Scanner scanner = new Scanner(System.in);
        Profile loggedUserProfile;
        ArrayList<Tweet> res;
        try {
            out.writeObject("get-profile");
            Thread.sleep(50);
            out.writeObject(loggedUser);
            Thread.sleep(50);
            loggedUserProfile = (Profile) in.readObject();
            res = (ArrayList<Tweet>) in.readObject();
        } catch (IOException | InterruptedException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        System.out.println(loggedUser.getFirstName() + " " + loggedUser.getLastName());
        System.out.println(loggedUser.getId());
        System.out.println("-----------------------------------------");
        // show header
        // show profile picture
        System.out.println(loggedUserProfile.getBio());
        System.out.println(loggedUserProfile.getLocation());
        System.out.println(loggedUserProfile.getWeb());
        String choice;
        int index=0;
        for (int i = res.size()-1; i >=0 ; i--) {
            System.out.println(res.size()-i + "." + res.get(i));
            index = i;
        }
        System.out.println("\na.select a tweet\nb.back");
        choice = scanner.nextLine();
        while (!choice.equals("a") && !choice.equals("b")) {
            System.out.println("invalid command!");
            choice = scanner.nextLine();
        }
        if (choice.equals("a")) {
            System.out.println("enter the number of tweet");
            int selectedClientIndex = scanner.nextInt();
            while (selectedClientIndex > res.size()) {
                System.out.println("invalid command!");
                selectedClientIndex = scanner.nextInt();
            }
            // todo to string tweet
        }
        else if(choice.equals("b")){
            ownProfile(loggedUser);
        }
    }
    public static void search(User loggedUser){
        Scanner scanner = new Scanner(System.in);
        System.out.println("SEARCH:");
        // todo choose between hashtag and user | piade sazi hashtag
        System.out.println("please enter your text");
        String searchingWord = scanner.nextLine();
        while (searchingWord==null){
            System.out.println("please enter text in a valid way");
            searchingWord = scanner.nextLine();
        }
        String answer;
        try {
            out.writeObject("search");
            Thread.sleep(50);
            out.writeObject(searchingWord);
            Thread.sleep(50);
            answer = (String) in.readObject();
            if(answer.equals("found")){
                ArrayList<User> res = (ArrayList<User>) in.readObject();
                for (int i=1;i<= res.size();i++){
                    System.out.println(i + "." + res.get(i-1));
                }
                System.out.println("\na.select a client\nb.back");
                String choice = scanner.nextLine();
                while (!choice.equals("a") && !choice.equals("b")){
                    System.out.println("invalid command!");
                    choice = scanner.nextLine();
                }
                if(choice.equals("a")){
                    System.out.println("enter the number of client");
                    int selectedClientIndex = scanner.nextInt();
                    while(selectedClientIndex > res.size()){
                        System.out.println("invalid command!");
                        selectedClientIndex = scanner.nextInt();
                    }
                    showOtherProfile(loggedUser,res.get(selectedClientIndex-1));
                }
                else if(choice.equals("b")){
                    login(loggedUser.getId());
                }
            }
            else if(answer.equals("not-found")){
                System.out.println("no result");
            }
        } catch (IOException | InterruptedException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
    public static void showOtherProfile(User loggedUser, User wantedUser){
        Scanner scanner = new Scanner(System.in);
        Profile wantedUserProfile;
        ArrayList<Tweet> wantedUserTweets;
        try {
            out.writeObject("get-profile");
            Thread.sleep(50);
            out.writeObject(wantedUser);
            Thread.sleep(50);
            wantedUserProfile = (Profile) in.readObject();
            wantedUserTweets = (ArrayList<Tweet>) in.readObject();
        } catch (IOException | InterruptedException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        System.out.println(wantedUser.getId());
        System.out.println(wantedUser.getFirstName() + " " + wantedUser.getLastName());
        System.out.println("------------------------------------------------");
        System.out.println("followings :" + wantedUserProfile.getFollowingNum() + "followers :" + wantedUserProfile.getFollowers());
        System.out.println(wantedUserProfile.getBio());
        System.out.println(wantedUserProfile.getWeb());
        System.out.println(wantedUserProfile.getLocation());
        String choice = " ";
        for (int i = wantedUserTweets.size()-1; i >=0 ; i--) {
            System.out.println(wantedUserTweets.size()-i + "." + wantedUserTweets.get(i));
        }
        System.out.println("\na.follow\tb.unfollow\nc.block\td.unblock");
        System.out.println("e.select a tweet\tf.back");
        while(!choice.equals("f")) {
            choice = scanner.nextLine();
            while (!choice.equals("a") && !choice.equals("b") &&
                    !choice.equals("c") && !choice.equals("d") &&
                    !choice.equals("e") && !choice.equals("f")) {
                System.out.println("invalid command!");
                choice = scanner.nextLine();
            }
            if (choice.equals("a")) {
                String answer;
                try {
                    out.writeObject("follow");
                    Thread.sleep(50);
                    out.writeObject(loggedUser);
                    Thread.sleep(50);
                    out.writeObject(wantedUser.getId());
                    Thread.sleep(50);
                    answer = (String) in.readObject();
                } catch (IOException | InterruptedException | ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
                if(answer.equals("already-followed")){
                    System.out.println("you are already following this user");
                }
                else if(answer.equals("success")){
                    System.out.println("user followed");
                }
                choice = " ";
            }
            if (choice.equals("b")) {
                String answer;
                try {
                    out.writeObject("unfollow");
                    Thread.sleep(50);
                    out.writeObject(loggedUser);
                    Thread.sleep(50);
                    out.writeObject(wantedUser.getId());
                    Thread.sleep(50);
                    answer = (String) in.readObject();
                } catch (IOException | InterruptedException | ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
                if(answer.equals("already-followed")){
                    System.out.println("you are not following this user");
                }
                else if(answer.equals("success")){
                    System.out.println("user unfollowed");
                }
                choice = " ";
            }
            if (choice.equals("c")) {
                String answer;
                try {
                    out.writeObject("block");
                    Thread.sleep(50);
                    out.writeObject(loggedUser);
                    Thread.sleep(50);
                    out.writeObject(wantedUser);
                    Thread.sleep(50);
                    answer = (String) in.readObject();
                } catch (IOException | InterruptedException | ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
                if(answer.equals("already-blocked")){
                    System.out.println("you have already blocked this user");
                }
                else if(answer.equals("success")){
                    System.out.println("user blocked");
                }
                choice = " ";
            }
            if (choice.equals("d")) {
                String answer;
                try {
                    out.writeObject("unblock");
                    Thread.sleep(50);
                    out.writeObject(loggedUser);
                    Thread.sleep(50);
                    out.writeObject(wantedUser);
                    Thread.sleep(50);
                    answer = (String) in.readObject();
                } catch (IOException | InterruptedException | ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
                if(answer.equals("have-not-blocked")){
                    System.out.println("you have not blocked this user");
                }
                else if(answer.equals("success")){
                    System.out.println("user unblocked");
                }
                choice = " ";
            }
            if (choice.equals("e")) {
                System.out.println("enter the number of tweet");
                int selectedTweetIndex = scanner.nextInt();
                while (selectedTweetIndex > wantedUserTweets.size()) {
                    System.out.println("invalid command!");
                    selectedTweetIndex = scanner.nextInt();
                }
                showTweet(wantedUserTweets.get(selectedTweetIndex-1),loggedUser);
                choice = " ";
            }
            if (choice.equals("f")) {
                choice = " ";
                login(loggedUser.getId());
            }
        }
    }
    public static void showTweet(Tweet theTweet,User userWhoIsWatching){
        Scanner scanner = new Scanner(System.in);
        User theUser;
        Profile UserProfile;
        try {
            out.writeObject("get-User");
            Thread.sleep(50);
            out.writeObject(theTweet.getUserId());
            Thread.sleep(50);
            theUser = (User) in.readObject();
            Thread.sleep(50);
            out.writeObject("get-profile");
            Thread.sleep(50);
            out.writeObject(theUser);
            Thread.sleep(50);
            UserProfile = (Profile) in.readObject();
        } catch (IOException | InterruptedException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        System.out.println(theUser.getId());
        System.out.println(theUser.getFirstName() + " " + theUser.getLastName() + " :");
        System.out.println("--------------------------------------------------");
        if(theTweet.getPicLink() != null){
            // show pic
        }
        System.out.println(theTweet.getText());
        long diffInMinutes = dateDifference(theTweet.getDate());
        if(diffInMinutes < 60){
            System.out.println(diffInMinutes +"m");
        }
        else if(diffInMinutes <= 1440){
            long diffInHours = diffInMinutes/60;
            System.out.println(diffInHours +"h");
        }
        else {
            System.out.println(theTweet.getDate());
        }
        System.out.println("1.likes :" + theTweet.getLikes() + "2.comments :" + theTweet.getComment());
        System.out.println( "3.retweets :" +theTweet.getRetweet() + "\t4.comment\n5.back");
        int choice = scanner.nextInt();
        while (choice != 5){
            if(choice == 1){
                String answer;
                try {
                    out.writeObject("like");
                    Thread.sleep(50);
                    out.writeObject(theUser);
                    Thread.sleep(50);
                    out.writeObject(theTweet);
                    Thread.sleep(50);
                    answer = (String) in.readObject();
                    Thread.sleep(50);
                } catch (IOException | InterruptedException | ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
                if(answer.equals("already-liked")){
                    System.out.println("tweet already liked!");
                }
                else if(answer.equals("success")){
                    System.out.println("tweet liked!");
                }
            }
            else if(choice == 2 ){
                ArrayList<String> comments;
                try {
                    out.writeObject("show-comments");
                    Thread.sleep(50);
                    out.writeObject(theTweet);
                    Thread.sleep(50);
                    comments = (ArrayList<String>) in.readObject();
                    Thread.sleep(50);
                } catch (IOException | InterruptedException | ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
                for (String comment : comments){
                    System.out.println(comment);
                }
            }
            else if(choice == 3){
                String answer;
                try {
                    out.writeObject("retweet");
                    Thread.sleep(50);
                    out.writeObject(theUser);
                    Thread.sleep(50);
                    out.writeObject(theTweet);
                    Thread.sleep(50);
                    answer = (String) in.readObject();
                    Thread.sleep(50);
                } catch (IOException | InterruptedException | ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
                if(answer.equals("already-retweeted")){
                    System.out.println("you already retweeted that tweet");
                }
                else if(answer.equals("success")){
                    System.out.println("done!");
                }
            }
            else if(choice == 4){
                String answer;
                System.out.println("please write your comment:");
                String comment = scanner.nextLine();
                while (comment==null || comment.length()>160){
                    System.out.println("invalid comment try again");
                    comment=scanner.nextLine();
                }
                try {
                    out.writeObject("comment");
                    Thread.sleep(50);
                    out.writeObject(userWhoIsWatching);
                    Thread.sleep(50);
                    out.writeObject(theTweet);
                    Thread.sleep(50);
                    out.writeObject(comment);
                    Thread.sleep(50);
                    answer=(String) in.readObject();
                } catch (IOException | InterruptedException | ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
                if(answer.equals("success")){
                    System.out.println("comment submitted");
                }
            }
            else if(choice==5){
                login(userWhoIsWatching.getId());
            }
        }
    }
    public static void newTweet(User loggedUser){
        Scanner scanner = new Scanner(System.in);
        System.out.println("1.just text\t2.just picture\t3.both");
        int choice = scanner.nextInt();
        String text="",picPath="";
        if(choice != 2){
            System.out.println("please enter your text");
            text = scanner.nextLine();
            while (text==null || text.length()>280){
                System.out.println("invalid text! try again");
                text = scanner.nextLine();
            }
        }
        if(choice != 1){
            System.out.println("please enter your pic path/link");
            picPath = scanner.nextLine();
            while(picPath == null){ // check size of pic
                System.out.println("invalid text! try again");
                picPath = scanner.nextLine();
            }
        }
        Tweet newTweet = new Tweet(text,picPath,loggedUser.getId());
        String answer;
        try {
            out.writeObject("new-tweet");
            Thread.sleep(50);
            out.writeObject(newTweet);
            answer = (String) in.readObject();
        } catch (IOException | InterruptedException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        if(answer.equals("success")){
            System.out.println("tweet uploaded");
        }
        login(loggedUser.getId());
    }
    public static void timeLine(User loggedUser){
        Scanner scanner = new Scanner(System.in);
        ArrayList<Tweet> tweets;
        try {
            out.writeObject("timeline");
            Thread.sleep(50);
            out.writeObject(loggedUser);
            Thread.sleep(50);
            tweets = (ArrayList<Tweet>) in.readObject();
        } catch (IOException | InterruptedException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        for (int i=1;i<=tweets.size();i++){
            System.out.println(i+"."+tweets.get(tweets.size()-i));
        }
        System.out.println("1.select a tweet\t2.back");
        int choice=scanner.nextInt();
        while (choice!=1 && choice!=2){
            System.out.println("invalid command try again");
            choice = scanner.nextInt();
        }
        if(choice==2){
            login(loggedUser.getId());
        }
        else {
            System.out.println("please enter number of your tweet");
            int selectedTweet =0;
            selectedTweet = scanner.nextInt();
            while (selectedTweet<=0 || selectedTweet > tweets.size()){
                System.out.println("invalid command try again");
                selectedTweet = scanner.nextInt();
            }
            showTweet(tweets.get(tweets.size()-selectedTweet),loggedUser);
        }
    }
    public static long dateDifference(Date tweetDate){
        Date now = new Date();

        long diffInMillies = Math.abs(now.getTime() - tweetDate.getTime());
        long diffInMinutes = Duration.ofMillis(diffInMillies).toMinutes();
        return diffInMinutes;
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
