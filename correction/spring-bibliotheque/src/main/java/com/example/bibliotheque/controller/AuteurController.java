
package com.example.bibliotheque.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.bibliotheque.jpa.repository.AuteurRepository;
import com.example.bibliotheque.jpa.repository.LivreRepository;

@Controller
@RequestMapping("/web/auteurs")
public class AuteurController {

    @Autowired
    private AuteurRepository auteurRepository;

    @Autowired
    private LivreRepository livreRepository;

    @GetMapping
    public String listAuteurs(Model model) {
        model.addAttribute("auteurs", auteurRepository.findAll());
        return "auteurs";
    }

    @GetMapping("/{id}/livres")
    public String listLivresByAuteur(@PathVariable Long id, Model model) {
        model.addAttribute("livres", livreRepository.findByAuteurId(id));
        return "livres";
    }
}
