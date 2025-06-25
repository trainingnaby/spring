package com.example.bibliotheque.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.bibliotheque.entity.Auteur;
import com.example.bibliotheque.jpa.repository.AuteurRepository;

@RestController
@RequestMapping("/api/auteurs")
public class AuteurRestController {

    @Autowired
    private AuteurRepository auteurRepository;

    @GetMapping
    public List<Auteur> getAllAuteurs() {
        return auteurRepository.findAll();
    }
}
