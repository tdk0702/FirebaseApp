package com.lab.firebaseapp;


//@IgnoreExtraProperties
public class User {

    public String name;
    public int number1;
    public int number2;
    public int sum;

    public User() {
    }
    public String Out(){
        return name + " " + sum;
    }
    public User(String s, int n1, int n2) {
        this.name = s;
        this.number1 = n1;
        this.number2 = n2;
        this.sum= n1 + n2;
    }
}
