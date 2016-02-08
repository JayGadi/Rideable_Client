package com.rideable.model;

import java.io.Serializable;

/**
 * Created by Jay on 10/29/2015.
 */
@SuppressWarnings("serial")
public class User implements Serializable {

    private String userEmail;
    private String userPassword;
    private String firstName;
    private String lastName;
    private String regId;

    public User(String firstName, String lastName, String userEmail, String password){
        this.firstName = firstName;
        this.lastName = lastName;
        this.userEmail = userEmail;
        this.userPassword = password;
    }

    public String getRegId() {return regId; }

    public void setRegId(String regId) {
        this.regId = regId;
    }

    public String getFirstName(){
        return firstName;
    }
    public String getLastName(){
        return lastName;
    }
    public String getUserEmail(){
        return userEmail;
    }
    public String getUserPassword(){
        return userPassword;
    }
    public void setFirstName(String firstName){
        this.firstName = firstName;
    }
    public void setLastName(String lastName){
        this.lastName = lastName;
    }
    public void setUserPassword(String password){
        userPassword = password;
    }
    public void setUserEmail(String email){
        userEmail = email;
    }

}
