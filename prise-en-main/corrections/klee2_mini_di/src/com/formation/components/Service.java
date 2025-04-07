package com.formation.components;

import com.formation.interfaces.DependanceAInjecter;
import com.formation.interfaces.BeanACreer;

@BeanACreer
public class Service {
	
    @DependanceAInjecter
    private Repository repository;

    public void getUsers() {
        System.out.println("getUsers depuis Service ...");
        repository.getUsers();
    }
}
