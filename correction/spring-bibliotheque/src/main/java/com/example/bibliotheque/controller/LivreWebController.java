
package com.example.bibliotheque.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.bibliotheque.entity.Livre;
import com.example.bibliotheque.jpa.repository.AuteurRepository;
import com.example.bibliotheque.jpa.repository.CategorieRepository;
import com.example.bibliotheque.jpa.repository.CritiqueRepository;
import com.example.bibliotheque.service.LivreService;

@Controller
@RequestMapping("/web/livres")
public class LivreWebController {

    @Autowired
    private LivreService livreService;

    @Autowired
    private CritiqueRepository critiqueRepository;

    @Autowired
    private AuteurRepository auteurRepository;

    @Autowired
    private CategorieRepository categorieRepository;

    @GetMapping
    public String listLivres(Model model) {
        model.addAttribute("livres", livreService.getAllLivres());
        return "livres";
    }

    @GetMapping("/sort/auteur")
    public String listLivresByAuteur(Model model) {
        model.addAttribute("livres", livreService.getLivresOrderByAuteur());
        return "livres";
    }

    @GetMapping("/sort/date")
    public String listLivresByDate(Model model) {
        model.addAttribute("livres", livreService.getLivresOrderByDate());
        return "livres";
    }

    @GetMapping("/sort/categorie")
    public String listLivresByCategorie(Model model) {
        model.addAttribute("livres", livreService.getLivresOrderByCategorie());
        return "livres";
    }

    @GetMapping("/new")
    public String newLivre(Model model) {
        model.addAttribute("livre", new Livre());
        model.addAttribute("auteurs", auteurRepository.findAll());
        model.addAttribute("categories", categorieRepository.findAll());
        return "livre_form";
    }

    @GetMapping("/edit/{id}")
    public String editLivre(@PathVariable Long id, Model model) {
        Livre livre = livreService.getLivreById(id);
        model.addAttribute("livre", livre);
        model.addAttribute("auteurs", auteurRepository.findAll());
        model.addAttribute("categories", categorieRepository.findAll());
        return "livre_form";
    }

    @PostMapping("/save")
    public String saveLivre(@ModelAttribute Livre livre) {
        livreService.saveLivre(livre);
        return "redirect:/web/livres";
    }

    @GetMapping("/delete/{id}")
    public String deleteLivre(@PathVariable Long id) {
        livreService.deleteLivre(id);
        return "redirect:/web/livres";
    }

    @GetMapping("/detail/{id}")
    public String detailLivre(@PathVariable Long id, Model model) {
        Livre livre = livreService.getLivreById(id);
        model.addAttribute("livre", livre);
        model.addAttribute("critiques", critiqueRepository.findByLivreId(id));
        return "livre_detail";
    }
}
