package client;

import java.io.Serializable;
import java.util.Date;

public class User implements Serializable {
    private String id;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String password;
    private String country;
    private String birthDate;
    private Date registerDate;
    private Date lastUpdate;
    private String profPicName;
    private String headerPicName;

    public User(String id, String firstName, String lastName, String email, String phoneNumber, String password,
                String country, String birthDate) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.password = password;
        this.country = country;
        this.birthDate = birthDate ;
        this.registerDate = new Date();
    }
    public User(String id,String password){
        this.id = id;
        this.password = password;
    }

    public String getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getPassword() {
        return password;
    }

    public String getCountry() {
        return country;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public Date getRegisterDate() {
        return registerDate;
    }

    public Date getLastUpdate() {
        return lastUpdate;
    }

    public String getProfPicName() {
        return profPicName;
    }

    public String getHeaderPicName() {
        return headerPicName;
    }
}
