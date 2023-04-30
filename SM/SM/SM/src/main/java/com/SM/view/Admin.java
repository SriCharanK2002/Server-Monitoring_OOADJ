package com.SM.view;

import com.SM.controller.Monitor;

public class Admin extends Personnel{
    Monitor mon;
    public Admin(String username, String email, String password){
        this.username=username;
        this.email=email;
        this.password=password;

    }
    public Monitor create_monitor(){
        if(mon == null){
            mon = new Monitor();
        }
        return mon;
    }

    void remove_monitor(Monitor mon){
        mon = null;
    }
}
