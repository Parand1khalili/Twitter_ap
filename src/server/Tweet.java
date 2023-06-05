package server;

import java.io.Serializable;
import java.util.Date;

public class Tweet implements Serializable,Comparable<Tweet>{
    private String text;
    private String picLink;
    private String userId;
    private int likes;
    private int retweet;
    private int comment;
    private Date date;
    private int isFavStar;
    private String likesIds;

    public Tweet(String text, String picLink, String userId) {
        this.text = text;
        this.picLink = picLink;
        this.userId = userId;
        this.date = new Date();
        this.isFavStar=0;
    }

    public Tweet(String text, String picLink, String userId, int likes, int retweet, int comment, Date date, int isFavStar) {
        this.text = text;
        this.picLink = picLink;
        this.userId = userId;
        this.likes = likes;
        this.retweet = retweet;
        this.comment = comment;
        this.date = date;
        this.isFavStar = isFavStar;
    }

    public String getLikesIds() {
        return likesIds;
    }

    public int getIsFavStar() {
        return isFavStar;
    }

    public void setIsFavStar(int isFavStar) {
        this.isFavStar = isFavStar;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getPicLink() {
        return picLink;
    }

    public void setPicLink(String picLink) {
        this.picLink = picLink;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    public int getRetweet() {
        return retweet;
    }

    public void setRetweet(int retweet) {
        this.retweet = retweet;
    }

    public int getComment() {
        return comment;
    }

    public void setComment(int comment) {
        this.comment = comment;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    @Override
    public int compareTo(Tweet o) {
        return this.date.compareTo(o.getDate());
    }
}
