package com.example.myprojectnew;

import java.util.PrimitiveIterator;

public class RegistrationData {

    private String name;
    private String email;
    private String password;

    public RegistrationData(String name, String email, String password){
        this.email=email;
        this.name=name;
        this.password=password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
    public RegistrationData(){

    }
}
