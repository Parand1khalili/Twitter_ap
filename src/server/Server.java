package server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.sql.*;
import java.util.concurrent.Executors;

public class Server implements Runnable{

    private ServerSocket serverSocket;
    private ExecutorService executorService;
    private boolean isDone;

    public Server() {
        this.isDone = false;
    }

    public static void main(String[] args) {

    }

    @Override
    public void run() {
        try {
            serverSocket = new ServerSocket(6666);
            while (!isDone){
            Socket client = serverSocket.accept();
            ClientHandler clientHandler = new ClientHandler(client);
            executorService = Executors.newCachedThreadPool();
            executorService.execute(clientHandler);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
class ClientHandler implements Runnable{

    private static ObjectInputStream in;
    private static ObjectOutputStream out;
    private Socket client;

    @Override
    public void run() {
        try {
            java.sql.Connection connection = DriverManager.getConnection("jdbc:sqlite:jdbc.db");
            Statement statement = connection.createStatement();
            out = new ObjectOutputStream(client.getOutputStream());
            in = new ObjectInputStream(client.getInputStream());

        } catch (SQLException | IOException e) {
            throw new RuntimeException(e);
        }
        try {
            String command;
            while(!(command = (String) in.readObject()).equals("exit") && (command = (String) in.readObject()) != null ) {
                if (command.equals("sign-up")) {
                    User x;
                    signUpServer(x=(User)in.readObject());
                }
                else if (command.equals("sign-in")) {
                    User y = (User)in.readObject() ;
                    signInServer(y.getId(),y.getPassword());
                }
                else if(command.equals("get-user")){
                    getUser((String) in.readObject());
                }
                else if(command.equals("profile")){
                    User x = (User)in.readObject();
                    profile(x);
                }
                else if(command.equals("edit-profile")){
                    User x=(User) in.readObject();
                    String y=(String) in.readObject();
                    editProfile(x,y);
                }
                else if(command.equals("edit-header")){
                    User x=(User) in.readObject();
                    String y=(String) in.readObject();
                    editHeader(x,y);
                }
                else if(command.equals("edit-bio")){
                    User x=(User) in.readObject();
                    String y=(String) in.readObject();
                    editProf(x,y,1);
                }
                else if(command.equals("edit-location")){
                    User x=(User) in.readObject();
                    String y=(String) in.readObject();
                    editProf(x,y,2);
                }
                else if(command.equals("edit-web")){
                    User x=(User) in.readObject();
                    String y=(String) in.readObject();
                    editProf(x,y,3);
                }
                else if(command.equals("follow")){
                    User x=(User) in.readObject();
                    String y=(String) in.readObject();
                    follow(x,y);
                }
                else if(command.equals("search")){
                    String x=(String) in.readObject();
                    search(x);
                }
                else if (command.equals("unfollow")){
                    User x=(User) in.readObject();
                    String y=(String) in.readObject();
                    unfollow(x,y);
                }
                else if ((command.equals("new-tweet"))){
                    Tweet x=(Tweet) in.readObject();
                    newTweet(x);
                }

            }
        } catch (IOException | ClassNotFoundException | SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public ClientHandler(Socket client) {
        this.client=client;
    }

    public static void signUpServer(User theUser) throws SQLException, IOException {
        java.sql.Connection connection = DriverManager.getConnection("jdbc:sqlite:jdbc.db");
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT * FROM user");
        String respond;
        if(theUser.getEmail()==null && theUser.getPhoneNumber()==null){
            respond = "empty-field";
            out.writeObject(respond);
            return;
        }
        while(resultSet.next()){
            if(resultSet.getString(1).equals(theUser.getId())){
                respond = "duplicate-id";
                out.writeObject(respond);
                return;
            }
            else if(resultSet.getString(4).equals(theUser.getEmail())){
                respond = "duplicate-email";
                out.writeObject(respond);
                return;
            }
            else if(resultSet.getString(5).equals(theUser.getPhoneNumber())){
                respond = "duplicate-number";
                out.writeObject(respond);
                return;
            }
        }
        statement.executeUpdate("INSERT INTO user(id,firstName,lastName,email,phoneNumber,password,country,birthDate,registerDate) " +
                "VALUES "+ theUser.getId()+theUser.getFirstName()+theUser.getLastName()+theUser.getEmail()+theUser.getPhoneNumber()+
                theUser.getPassword()+theUser.getCountry()+theUser.getBirthDate()+theUser.getRegisterDate());
        respond = "success";
        out.writeObject(respond);
    }
    public static void signInServer(String id, String pass) throws SQLException, IOException {
        java.sql.Connection connection = DriverManager.getConnection("jdbc:sqlite:jdbc.db");
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT * FROM user");
        String respond;
        while (resultSet.next()){
            if(resultSet.getString(1).equals(id)){
                if(!resultSet.getString(6).equals(pass)){
                    respond="wrong-pass";
                    out.writeObject(respond);
                    return;
                }
                else{
                    respond="success";
                    out.writeObject(respond);
                    return;
                }
            }
        }
        respond="not-found";
        out.writeObject(respond);
    }
    public static void profile(User theUser) throws SQLException, IOException {
        java.sql.Connection connection = DriverManager.getConnection("jdbc:sqlite:jdbc.db");
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT * FROM user");
        while (resultSet.next()){
            if(resultSet.getString(1).equals(theUser.getId())){
                Profile theProfile = new Profile(resultSet.getString(11),resultSet.getString(12),
                        resultSet.getString(13),resultSet.getString(14),resultSet.getString(15));
                out.writeObject(theProfile);
                return;
            }
        }
    }
    public static void editProfile(User theUser,String prof) throws SQLException, IOException {
        java.sql.Connection connection = DriverManager.getConnection("jdbc:sqlite:jdbc.db");
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT * FROM user");
        while (resultSet.next()){
            if(resultSet.getString(1).equals(theUser.getId())){
                statement.executeUpdate("INSERT INTO user(profilePicture)"+"VALUES "+prof);
                Profile theProfile = new Profile(resultSet.getString(11),resultSet.getString(12),
                        resultSet.getString(13),resultSet.getString(14),resultSet.getString(15));
                out.writeObject(theProfile);
                return;
            }
        }

    }
    public static void editHeader(User theUser,String header) throws SQLException, IOException {
        java.sql.Connection connection = DriverManager.getConnection("jdbc:sqlite:jdbc.db");
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT * FROM user");
        while (resultSet.next()){
            if(resultSet.getString(1).equals(theUser.getId())){
                statement.executeUpdate("INSERT INTO user(header)"+"VALUES "+header);
                Profile theProfile = new Profile(resultSet.getString(11),resultSet.getString(12),
                        resultSet.getString(13),resultSet.getString(14),resultSet.getString(15));
                out.writeObject(theProfile);
                return;
            }
        }
    }
    public static void getUser(String id) throws SQLException, IOException {
        java.sql.Connection connection = DriverManager.getConnection("jdbc:sqlite:jdbc.db");
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT * FROM user");
        while (resultSet.next()){
            if(resultSet.getString(1).equals(id)){
                User theUser=new User(resultSet.getString(1),resultSet.getString(2),
                        resultSet.getString(3),resultSet.getString(4),resultSet.getString(5)
                ,resultSet.getString(6),resultSet.getNString(7),resultSet.getString(8));
                out.writeObject(theUser);
            }
        }
    }
    public static void editProf(User theUser,String text,int com) throws SQLException, IOException {
        java.sql.Connection connection = DriverManager.getConnection("jdbc:sqlite:jdbc.db");
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT * FROM user");
        if(com==1){
            //bio
            while (resultSet.next()){
                if(resultSet.getString(1).equals(theUser.getId())){
                    statement.executeUpdate("INSERT INTO user(bio)"+"VALUES "+text);
                    Profile theProfile = new Profile(resultSet.getString(11),resultSet.getString(12),
                            resultSet.getString(13),resultSet.getString(14),resultSet.getString(15));
                    out.writeObject(theProfile);
                    return;
                }
            }
        }
        else if(com==2){
            //loc
            while (resultSet.next()){
                if(resultSet.getString(1).equals(theUser.getId())){
                    statement.executeUpdate("INSERT INTO user(location)"+"VALUES "+text);
                    Profile theProfile = new Profile(resultSet.getString(11),resultSet.getString(12),
                            resultSet.getString(13),resultSet.getString(14),resultSet.getString(15));
                    out.writeObject(theProfile);
                    return;
                }
            }
        }
        else if(com==3){
            //web
            while (resultSet.next()){
                if(resultSet.getString(1).equals(theUser.getId())){
                    statement.executeUpdate("INSERT INTO user(web)"+"VALUES "+text);
                    Profile theProfile = new Profile(resultSet.getString(11),resultSet.getString(12),
                            resultSet.getString(13),resultSet.getString(14),resultSet.getString(15));
                    out.writeObject(theProfile);
                    return;
                }
            }
        }
    }
    public static void follow(User theUser,String followingId) throws SQLException, IOException {
        java.sql.Connection connection = DriverManager.getConnection("jdbc:sqlite:jdbc.db");
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT * FROM user");
        String respond;
        while (resultSet.next()){
            if(resultSet.getString(1).equals(theUser.getId())){
                if(resultSet.getString(17).contains(followingId)){
                    respond="already-followed";
                    out.writeObject(respond);
                    return;
                }
            }
            else{
                statement.executeUpdate("INSERT INTO user(followings)"+"VALUES "+theUser.getFollowing()+"="+followingId);
                while (resultSet.next()){
                    if(resultSet.getString(1).equals(followingId)){
                        statement.executeUpdate("INSERT INTO user(followers)"+"VALUES "+resultSet.getString(16)+"="+theUser.getId());
                        respond="success";
                        out.writeObject(respond);
                        return;
                    }
                }
            }
        }
    }
    public static void search(String text) throws SQLException, IOException {
        ArrayList <User> res=new ArrayList<>();
        java.sql.Connection connection = DriverManager.getConnection("jdbc:sqlite:jdbc.db");
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT * FROM user");
        String respond;
        while (resultSet.next()){
            if(resultSet.getString(1).contains(text)||resultSet.getString(2).contains(text)
                    ||resultSet.getString(3).contains(text)){
                User newUser = new User(resultSet.getString(1),resultSet.getString(2),
                        resultSet.getString(3),resultSet.getString(4),resultSet.getString(5),
                        resultSet.getString(6),resultSet.getString(7),resultSet.getString(8));
                res.add(newUser);
            }
        }
        if(res.isEmpty()){
            respond="not-found";
            out.writeObject(respond);
            return;
        }
        else{
            out.writeObject(res);
            return;
        }
    }
    public static void unfollow(User theUser,String followingId) throws SQLException, IOException {
        java.sql.Connection connection = DriverManager.getConnection("jdbc:sqlite:jdbc.db");
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT * FROM user");
        String respond;
        while (resultSet.next()){
            if(resultSet.getString(1).equals(theUser.getId())){
                if(!resultSet.getString(17).contains(followingId)){
                    respond="is not followed";
                    out.writeObject(respond);
                    return;
                }
                else {
                    int i;
                    String[] following=resultSet.getString(17).split("=");
                    ArrayList<String> list = new ArrayList<String>(Arrays.asList(following));
                    for( i=0;i<list.size();i++){
                        if(list.equals(followingId)){
                            break;
                        }
                        i++;
                    }
                    list.remove(i);
                    statement.executeUpdate("INSERT INTO user(followings)"+"VALUES "+list);
                }
            }
        }
        while (resultSet.next()){
            if(resultSet.getString(1).equals(followingId)){
                int i;
                String[] follower=resultSet.getString(16).split("=");
                ArrayList<String> list = new ArrayList<>(Arrays.asList(follower));
                for(i=0;i<list.size();i++){
                    if(list.equals(theUser.getId())){
                        break;
                    }
                    i++;
                }
                list.remove(i);
                statement.executeUpdate("INSERT INTO user(followers)"+"VALUES "+list);
                respond="success";
                out.writeObject(respond);
                return;
            }
        }
    }
    public static void newTweet(Tweet tweet) throws SQLException, IOException {
        java.sql.Connection connection = DriverManager.getConnection("jdbc:sqlite:jdbc.db");
        Statement statement = connection.createStatement();
        statement.executeUpdate("INSERT INTO Tweet(text, picture, userid, like, retweet, comment, date) "+"VALUES "
        +tweet.getText()+tweet.getPicLink()+tweet.getUserId()+tweet.getLikes()+tweet.getRetweet()+tweet.getComment()+tweet.getDate());
        String respond="success";
        out.writeObject(respond);
    }
}





