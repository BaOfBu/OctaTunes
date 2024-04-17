package com.example.octatunes.Model;

import java.util.Date;

public class UsersModel {
    private int UserID;
    private String Name;
    private String Email;
    private String Password;
    private Date DateOfBirth;
    private String Image;

    public UsersModel(int userID, String name, String email, String password, Date dateOfBirth, String image) {
        UserID = userID;
        Name = name;
        Email = email;
        Password = password;
        DateOfBirth = dateOfBirth;
        Image = image;
    }

    public int getUserID() {
        return UserID;
    }

    public void setUserID(int userID) {
        UserID = userID;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public Date getDateOfBirth() {
        return DateOfBirth;
    }

    public void setDateOfBirth(Date dateOfBirth) {
        DateOfBirth = dateOfBirth;
    }

    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        Image = image;
    }
}
