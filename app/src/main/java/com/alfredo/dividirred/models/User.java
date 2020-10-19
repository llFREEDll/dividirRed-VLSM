package com.alfredo.dividirred.models;

public class User {
    private String user, password;

    public  User(String user,String password){
        this.user = user;
        this.password = password;

    }

    public String getUser() {
        return user;
    }

    public String getPassword() {
        return password;
    }
}
