package server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.concurrent.ExecutorService;
import java.sql.*;
import java.util.concurrent.Executors;
import java.util.Date;


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
                else if(command.equals("get-profile")){
                    User x = (User)in.readObject();
                    User y = (User)in.readObject();
                    getProfile(x,y);
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
                else if(command.equals("timeline")){
                    User x=(User) in.readObject();
                    timeline(x);
                }
                else if(command.equals("like")){
                    User x=(User) in.readObject();
                    Tweet y=(Tweet) in.readObject();
                    like(x,y);
                }
                else if(command.equals("hashtag")){
                    String x=(String) in.readObject();
                    searchHashtag(x);
                }
                else if(command.equals("block")){
                    User x=(User) in.readObject();
                    User y=(User) in.readObject();
                    block(x,y);
                }
                else if(command.equals("unblock")){
                    User x=(User) in.readObject();
                    User y=(User) in.readObject();
                    unblock(x,y);
                }
                else if (command.equals("retweet")){
                    User x=(User) in.readObject();
                    Tweet y=(Tweet) in.readObject();
                    retweet(x,y);
                }
                else if(command.equals("comment")){
                    //reply
                    User x=(User) in.readObject();
                    Tweet y=(Tweet) in.readObject();
                    String z=(String) in.readObject();
                    addComment(x,y,z);
                }
                else if(command.equals("show-comments")){
                    Tweet x=(Tweet) in.readObject();
                    showComments(x);
                }

            }
        } catch (IOException | ClassNotFoundException | SQLException | InterruptedException | ParseException e) {
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
    public static void getProfile(User theUser,User wanted) throws SQLException, IOException {
        java.sql.Connection connection = DriverManager.getConnection("jdbc:sqlite:jdbc.db");
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT * FROM user");//wantedUser
        ResultSet resultSet1 = statement.executeQuery("SELECT * FROM user");//theUser
        while (resultSet1.next()){
            if(resultSet1.getString(1).equals(theUser.getId())){
                if(!resultSet1.getString(20).contains(wanted.getId())){
                    while (resultSet.next()){
                        if(resultSet.getString(1).equals(wanted.getId())){
                            Profile theProfile = new Profile(resultSet.getString(11),resultSet.getString(12),
                                    resultSet.getString(13),resultSet.getString(14),resultSet.getString(15),
                                    resultSet.getInt(18),resultSet.getInt(19));
                            out.writeObject(theProfile);
                        }
                    }
                }
            }
        }
        ResultSet resultSetTweet = statement.executeQuery("SELECT * FROM Tweet");
        while (resultSetTweet.next()){
            if(resultSetTweet.getString(3).equals(wanted.getId())){
                out.writeObject(resultSetTweet);
            }
        }
    }
    public static void editProfile(User theUser,String prof) throws SQLException, IOException {
        java.sql.Connection connection = DriverManager.getConnection("jdbc:sqlite:jdbc.db");
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT * FROM user");
        while (resultSet.next()){
            if(resultSet.getString(1).equals(theUser.getId())){
                statement.executeUpdate("UPDATE user SET profilePicture = '" + prof + "' WHERE id = " + theUser.getId());
                Profile theProfile = new Profile(resultSet.getString(11),resultSet.getString(12),
                        resultSet.getString(13),resultSet.getString(14),resultSet.getString(15),
                        resultSet.getInt(18),resultSet.getInt(19));
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
                statement.executeUpdate("UPDATE user SET header = '" + header + "' WHERE id = " + theUser.getId());
                Profile theProfile = new Profile(resultSet.getString(11),resultSet.getString(12),
                        resultSet.getString(13),resultSet.getString(14),resultSet.getString(15),
                        resultSet.getInt(18),resultSet.getInt(19));
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
                    statement.executeUpdate("UPDATE user SET bio = '" + text + "' WHERE id = " + theUser.getId());
                    Profile theProfile = new Profile(resultSet.getString(11),resultSet.getString(12),
                            resultSet.getString(13),resultSet.getString(14),resultSet.getString(15),
                            resultSet.getInt(18),resultSet.getInt(19));
                    out.writeObject(theProfile);
                    return;
                }
            }
        }
        else if(com==2){
            //loc
            while (resultSet.next()){
                if(resultSet.getString(1).equals(theUser.getId())){
                    statement.executeUpdate("UPDATE user SET location = '" + text + "' WHERE id = " + theUser.getId());
                    Profile theProfile = new Profile(resultSet.getString(11),resultSet.getString(12),
                            resultSet.getString(13),resultSet.getString(14),resultSet.getString(15)
                    ,resultSet.getInt(18),resultSet.getInt(19));
                    out.writeObject(theProfile);
                    return;
                }
            }
        }
        else if(com==3){
            //web
            while (resultSet.next()){
                if(resultSet.getString(1).equals(theUser.getId())){
                    statement.executeUpdate("UPDATE user SET web = '" + text + "' WHERE id = " + theUser.getId());
                    Profile theProfile = new Profile(resultSet.getString(11),resultSet.getString(12),
                            resultSet.getString(13),resultSet.getString(14),resultSet.getString(15)
                    ,resultSet.getInt(18),resultSet.getInt(19));
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
                statement.executeUpdate("UPDATE user SET followings = '" + theUser.getFollowing()+"="+followingId + "' WHERE id = " + theUser.getId());
                statement.executeUpdate("UPDATE user SET followingNum = '" + (theUser.getFollowingNum()+1) + "' WHERE id = " + theUser.getId());
                while (resultSet.next()){
                    if(resultSet.getString(1).equals(followingId)){
                        statement.executeUpdate("UPDATE user SET followers = '" + resultSet.getString(16)+"="+theUser.getId() + "' WHERE id = " + followingId);
                        statement.executeUpdate("UPDATE user SET followerNum = '" + (Integer.parseInt(resultSet.getString(18))+1)+ "' WHERE id = " + followingId);
                        respond="success";
                        out.writeObject(respond);
                        return;
                    }
                }
            }
        }
    }
    public static void search(String text) throws SQLException, IOException, InterruptedException {
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
        }
        else{
            respond="found";
            out.writeObject(respond);
            Thread.sleep(50);
            out.writeObject(res);
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
                    statement.executeUpdate("UPDATE user SET followings = '" +list+ "' WHERE id = " + theUser.getId());
                    statement.executeUpdate("UPDATE user SET followingNum = '" + (theUser.getFollowingNum()-1) + "' WHERE id = " + theUser.getId());
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
                statement.executeUpdate("UPDATE user SET followers = '" +list+ "' WHERE id = " + followingId);
                statement.executeUpdate("UPDATE user SET followerNum = '" +(Integer.parseInt(resultSet.getString(18))-1)+ "' WHERE id = " + followingId);
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
    public static void timeline(User theUser) throws SQLException, ParseException, IOException {
        java.sql.Connection connection = DriverManager.getConnection("jdbc:sqlite:jdbc.db");
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT * FROM user");
        ResultSet resultSetTweet = statement.executeQuery("SELECT * FROM Tweet");
        ArrayList <Tweet> res=new ArrayList<>();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

        //check followings
        while (resultSet.next()){
            if(resultSet.getString(1).equals(theUser.getId())){
                while (resultSetTweet.next()){
                    if(resultSet.getString(17).contains(resultSetTweet.getString(3))){
                        Tweet theTweet = new Tweet(resultSetTweet.getString(1),resultSet.getString(2),
                                resultSet.getString(3),Integer.parseInt(resultSet.getString(4)),Integer.parseInt(resultSet.getString(5)),
                               Integer.parseInt( resultSet.getString(6)),format.parse( resultSet.getString(7)) ,Integer.parseInt(resultSet.getString(8)) );
                        res.add(theTweet);
                    }
                }
            }
        }

        //check favstars and blocks
        while (resultSetTweet.next()){
            if(resultSetTweet.getString(8).equals("1")){
                Tweet theTweet = new Tweet(resultSetTweet.getString(1),resultSet.getString(2),
                        resultSet.getString(3),Integer.parseInt(resultSet.getString(4)),Integer.parseInt(resultSet.getString(5)),
                        Integer.parseInt( resultSet.getString(6)),format.parse( resultSet.getString(7)) ,Integer.parseInt(resultSet.getString(8)) );
                if(!res.contains(theTweet)){
                    while (resultSet.next()){
                        if(resultSet.getString(1).equals(theUser.getId())){
                            if(!resultSet.getString(20).contains(theTweet.getUserId())){
                            res.add(theTweet);
                            }
                        }
                    }
                }
            }
        }
        out.writeObject(res);
    }
    public static void like(User theUser,Tweet theTweet) throws SQLException, IOException {
        java.sql.Connection connection = DriverManager.getConnection("jdbc:sqlite:jdbc.db");
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT * FROM user");
        ResultSet resultSetTweet = statement.executeQuery("SELECT * FROM Tweet");
        String respond;
        while (resultSetTweet.next()){
            if(resultSetTweet.equals(theTweet) && resultSetTweet.getString(9).contains(theUser.getId())){
                respond="already-liked";
                out.writeObject(respond);
            }
            else if(resultSetTweet.equals(theTweet) && ! resultSetTweet.getString(9).contains(theUser.getId())){
                respond="success";
                statement.executeUpdate("UPDATE Tweet SET likedIds = '" +theTweet.getLikesIds()+"="+theUser.getId()+ "'WHERE text = " + theTweet.getText()+ "' AND userid = '"+theTweet.getUserId());
                statement.executeUpdate("UPDATE Tweet SET like = '" +(Integer.parseInt(resultSetTweet.getString(4))+1)+"'WHERE text = " + theTweet.getText()+ "' AND userid = '"+theTweet.getUserId());
                if(Integer.parseInt(resultSetTweet.getString(4))==10){
                    statement.executeUpdate("UPDATE Tweet SET isFavStar = '" +(theTweet.getIsFavStar()+1)+ "'WHERE text = " + theTweet.getText()+ "' AND userid = '"+theTweet.getUserId());
                }
                out.writeObject(respond);
            }
        }
    }
    public static void searchHashtag(String hashtag) throws SQLException, ParseException, IOException {
        java.sql.Connection connection = DriverManager.getConnection("jdbc:sqlite:jdbc.db");
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT * FROM Tweet");
        ArrayList<Tweet> res=new ArrayList<>();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        while (resultSet.next()){
            if(resultSet.getString(1).contains("#"+hashtag)){
                Tweet theTweet = new Tweet(resultSet.getString(1),resultSet.getString(2),
                        resultSet.getString(3),Integer.parseInt(resultSet.getString(4)),
                        Integer.parseInt(resultSet.getString(5)),
                        Integer.parseInt( resultSet.getString(6)),format.parse( resultSet.getString(7))
                        ,Integer.parseInt(resultSet.getString(8)) );
                res.add(theTweet);
            }
        }
        out.writeObject(res);
    }
    public static void block(User theUser,User block) throws SQLException, IOException {
        java.sql.Connection connection = DriverManager.getConnection("jdbc:sqlite:jdbc.db");
        Statement statement = connection.createStatement();
        ResultSet resultSetUser = statement.executeQuery("SELECT * FROM user");
        ResultSet resultSetBlock = statement.executeQuery("SELECT * FROM user");
        String respond ;
        while (resultSetUser.next()){
            if(resultSetUser.getString(1).equals(theUser.getId()) &&
                    resultSetUser.getString(20).contains(block.getId())){
                respond="already-blocked";
                out.writeObject(respond);
                return;
            }
            else if(resultSetUser.getString(1).equals(theUser.getId())){
                statement.executeUpdate("UPDATE user SET blacklist = '" +(theUser.getBlacklist())+"="+block.getId()+ "' WHERE id = " + theUser.getId());
                respond="success";
                out.writeObject(respond);
            }
        }
        while (resultSetUser.next()){
            if(resultSetUser.getString(1).equals(theUser.getId()) && resultSetUser.getString(16).contains(block.getId())){
                //block is following the user
                //block should be removed from the user followers
                int i;
                String[] follower=resultSetUser.getString(16).split("=");
                ArrayList<String> list = new ArrayList<String>(Arrays.asList(follower));
                for( i=0;i<list.size();i++){
                    if(list.equals(block.getId())){
                        break;
                    }
                    i++;
                }
                list.remove(i);
                statement.executeUpdate("UPDATE user SET followers = '" +list+ "' WHERE id = " + theUser.getId());
                statement.executeUpdate("UPDATE user SET followerNum = '" +(theUser.getFollowerNum()-1)+ "' WHERE id = " + theUser.getId());

                //the user should be removed from the block followings
                while (resultSetBlock.next()){
                    if(resultSetBlock.getString(1).equals(block.getId())){
                        int j;
                        String[] followings=resultSetUser.getString(17).split("=");
                        ArrayList<String> list2 = new ArrayList<String>(Arrays.asList(followings));
                        for( j=0;j<list2.size();j++){
                            if(list2.equals(theUser.getId())){
                                break;
                            }
                            j++;
                        }
                        list2.remove(j);
                        statement.executeUpdate("UPDATE user SET followings = '" +list2+ "' WHERE id = " + block.getId());
                        statement.executeUpdate("UPDATE user SET followingNum = '" +(block.getFollowingNum()-1)+ "' WHERE id = " + block.getId());
                    }
                }
            }
            else if(resultSetUser.getString(1).equals(theUser.getId()) && resultSetUser.getString(17).contains(block.getId())){
                //user is following block
                //user should be removed from the block followers
                while (resultSetBlock.next()){
                    if(resultSetBlock.getString(1).equals(block.getId())){
                        int i;
                        String[] follower=resultSetBlock.getString(16).split("=");
                        ArrayList<String> list = new ArrayList<String>(Arrays.asList(follower));
                        for( i=0;i<list.size();i++){
                            if(list.equals(theUser.getId())){
                                break;
                            }
                            i++;
                        }
                        list.remove(i);
                        statement.executeUpdate("UPDATE user SET followers = '" +list+ "' WHERE id = " + block.getId());
                        statement.executeUpdate("UPDATE user SET followerNum = '" +(block.getFollowerNum()-1)+ "' WHERE id = " + block.getId());
                    }
                }

                //block should be removed from the user followings
                while (resultSetUser.next()){
                    if(resultSetUser.getString(1).equals(theUser.getId())){
                        int j;
                        String[] followings=resultSetUser.getString(17).split("=");
                        ArrayList<String> list2 = new ArrayList<String>(Arrays.asList(followings));
                        for( j=0;j<list2.size();j++){
                            if(list2.equals(block.getId())){
                                break;
                            }
                            j++;
                        }
                        list2.remove(j);
                        statement.executeUpdate("UPDATE user SET followings = '" +list2+ "' WHERE id = " + theUser.getId());
                        statement.executeUpdate("UPDATE user SET followingNum = '" +(theUser.getFollowingNum()-1)+ "' WHERE id = " + theUser.getId());
                    }
                }
            }
        }
    }
    public static void unblock(User theUser,User unblock) throws SQLException, IOException {
        java.sql.Connection connection = DriverManager.getConnection("jdbc:sqlite:jdbc.db");
        Statement statement = connection.createStatement();
        ResultSet resultSetUser = statement.executeQuery("SELECT * FROM user");
        String respond;
        while (resultSetUser.next()){
            if(resultSetUser.getString(1).equals(theUser.getId()) && !resultSetUser.getString(20).contains(unblock.getId())){
                respond="have-not-blocked";
                out.writeObject(respond);
                return;
            }
            if(resultSetUser.getString(1).equals(theUser.getId())  && resultSetUser.getString(20).contains(unblock.getId())){
                int i;
                String[] blacklist=resultSetUser.getString(20).split("=");
                ArrayList<String> list = new ArrayList<String>(Arrays.asList(blacklist));
                for( i=0;i<list.size();i++){
                    if(list.equals(unblock.getId())){
                        break;
                    }
                    i++;
                }
                list.remove(i);
                statement.executeUpdate("UPDATE user SET blacklist = '" +list+ "' WHERE id = " + theUser.getId());
                respond="success";
                out.writeObject(respond);
                return;
            }
        }
    }
    public static void retweet(User theUser,Tweet theTweet) throws SQLException, IOException {
        java.sql.Connection connection = DriverManager.getConnection("jdbc:sqlite:jdbc.db");
        Statement statement = connection.createStatement();
        statement.executeUpdate("INSERT INTO Tweet(text, picture, userid, like, retweet, comment, date) "+"VALUES "
                +theTweet.getText()+theTweet.getPicLink()+theUser.getId()+theTweet.getLikes()+theTweet.getRetweet()+theTweet.getComment()+new Date());
        statement.executeUpdate("UPDATE Tweet SET retweet = '" +(theTweet.getRetweet()+1)+ "'WHERE text = " + theTweet.getText()+ "' AND userid = '"+theTweet.getUserId());
        String respond="success";
        out.writeObject(respond);
    }
    public static void addComment(User theUser,Tweet theTweet,String comment) throws SQLException, IOException {
        java.sql.Connection connection = DriverManager.getConnection("jdbc:sqlite:jdbc.db");
        Statement statement = connection.createStatement();
        statement.executeUpdate("UPDATE Tweet SET comment = '" +(theTweet.getComment()+1)+ "'WHERE text = " + theTweet.getText()+ "' AND userid = '"+theTweet.getUserId());
        String respond;
        statement.executeUpdate("UPDATE Tweet SET comments = '" +"@"+theUser.getId()+"="+comment+ "'WHERE text = " + theTweet.getText()+ "' AND userid = '"+theTweet.getUserId());
        //@id=comment
        statement.executeUpdate("UPDATE Tweet SET comment = '" +(theTweet.getComment()+1)+ "'WHERE text = " + theTweet.getText()+ "' AND userid = '"+theTweet.getUserId());
        respond="success";
        out.writeObject(respond);
    }
    public static void showComments(Tweet tweet) throws SQLException, IOException {
        java.sql.Connection connection = DriverManager.getConnection("jdbc:sqlite:jdbc.db");
        Statement statement = connection.createStatement();
        ResultSet resultSet= statement.executeQuery("SELECT * FROM Tweet");
        while (resultSet.next()){
            if(resultSet.equals(tweet)){
                String str=resultSet.getString(10);
                String[] arr = str.split("@");
                ArrayList<String> list = new ArrayList<>(Arrays.asList(arr));
                out.writeObject(list);
                return;
            }
        }
    }

}