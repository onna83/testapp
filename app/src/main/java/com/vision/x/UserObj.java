package com.vision.x;

public class UserObj {
    private

    String Name,Phone,UserID ;

    public UserObj(String Name, String Phone, String UserID){
        this.Name=Name;
        this.Phone=Phone;
        this.UserID=UserID;
    }
    String getName(){return Name;}
    String getPhone(){return Phone;}
    String getUserID(){return UserID;}

    public void setName(String name) {
        this.Name = name;
    }
}
