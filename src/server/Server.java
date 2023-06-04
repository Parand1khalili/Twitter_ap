package server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
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
                else if(command.equals("get-user")){
                    getUser((String) in.readObject());
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
}





