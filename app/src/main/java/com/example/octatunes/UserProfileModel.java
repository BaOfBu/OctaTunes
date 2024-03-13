package com.example.octatunes;

public class UserProfileModel {
    private int userImageId;
    private String fullname;
    public String getFullName(){
        return fullname;
    }
    public void setFullName(String fullname){
        this.fullname = fullname;
    }
    public int getUserImageId(){
        return userImageId;
    }
    public void setUserImageId(int userImageId){
        this.userImageId = userImageId;
    }

    public UserProfileModel(int userImageId, String fullname){
        this.userImageId = userImageId;
        this.fullname = fullname;
    }
}
