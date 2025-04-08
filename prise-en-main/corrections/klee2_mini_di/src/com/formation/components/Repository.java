package com.formation.components;

import com.formation.interfaces.BeanACreer;
import com.formation.interfaces.DoitEtrePoli;

@BeanACreer
public class Repository {
    public void getUsers() {
        System.out.println("getUsers depuis Repository ...");
    }
}

