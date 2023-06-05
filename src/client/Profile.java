package client;

import java.util.ArrayList;

public class Profile {
    private String picLink;
    private String headerLink;
    private String bio;
    private String location;
    private String web;
    private int followerNum;
    private int followingNum;
    private ArrayList<String> followers=new ArrayList<>();
    private ArrayList<String> followings=new ArrayList<>();
    private ArrayList<Tweet> tweets=new ArrayList<>();


    public Profile(String picLink, String headerLink, String bio, String location, String web, int followerNum, int followingNum) {
        this.picLink = picLink;
        this.headerLink = headerLink;
        this.bio = bio;
        this.location = location;
        this.web = web;
        this.followerNum = followerNum;
        this.followingNum = followingNum;
    }

    public int getFollowerNum() {
        return followerNum;
    }

    public int getFollowingNum() {
        return followingNum;
    }

    public String getPicLink() {
        return picLink;
    }

    public void setPicLink(String picLink) {
        this.picLink = picLink;
    }

    public String getHeaderLink() {
        return headerLink;
    }

    public void setHeaderLink(String headerLink) {
        this.headerLink = headerLink;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getWeb() {
        return web;
    }

    public void setWeb(String web) {
        this.web = web;
    }

    public ArrayList<String> getFollowers() {
        return followers;
    }

    public void setFollowers(ArrayList<String> followers) {
        this.followers = followers;
    }

    public ArrayList<String> getFollowings() {
        return followings;
    }

    public void setFollowings(ArrayList<String> followings) {
        this.followings = followings;
    }

    public ArrayList<Tweet> getTweets() {
        return tweets;
    }

    public void setTweets(ArrayList<Tweet> tweets) {
        this.tweets = tweets;
    }
}
