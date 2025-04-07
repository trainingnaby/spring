package com.formation.components;

import com.formation.interfaces.BeanACreer;

@BeanACreer
public class Repository {
    public void getUsers() {
        System.out.println("getUsers depuis Repository ...");
    }
}

