package com.example.bibliotheque.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.bibliotheque.jpa.repository.CategorieRepository;
import com.example.bibliotheque.jpa.repository.LivreRepository;

@Controller
@RequestMapping("/web/categories")
public class CategorieController {

    @Autowired
    private CategorieRepository categorieRepository;

    @Autowired
    private LivreRepository livreRepository;

    @GetMapping("/{id}/livres")
    public String listLivresByCategorie(@PathVariable Long id, Model model) {
        model.addAttribute("livres", livreRepository.findAll().stream()
            .filter(l -> l.getCategorie() != null && l.getCategorie().getId().equals(id))
            .toList());
        return "livres";
    }
}
