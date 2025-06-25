package com.example.bibliotheque.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.bibliotheque.entity.Critique;
import com.example.bibliotheque.jpa.repository.CritiqueRepository;
import com.example.bibliotheque.jpa.repository.LivreRepository;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/web/critiques")
public class CritiqueWebController {

    @Autowired
    private CritiqueRepository critiqueRepository;

    @Autowired
    private LivreRepository livreRepository;

    @GetMapping
    public String listCritiques(Model model, @ModelAttribute("message") String message) {
        model.addAttribute("critiques", critiqueRepository.findAll());
        model.addAttribute("message", message);
        return "critiques";
    }

    @GetMapping("/new")
    public String newCritique(Model model) {
        model.addAttribute("critique", new Critique());
        model.addAttribute("livres", livreRepository.findAll());
        return "critique_form";
    }

    @PostMapping("/save")
    public String saveCritique(@Valid @ModelAttribute Critique critique,
                               BindingResult result,
                               Model model,
                               RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("livres", livreRepository.findAll());
            return "critique_form";
        }
        critiqueRepository.save(critique);
        redirectAttributes.addFlashAttribute("message", "Critique enregistrée avec succès !");
        return "redirect:/web/critiques";
    }

    @GetMapping("/delete/{id}")
    public String deleteCritique(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        critiqueRepository.deleteById(id);
        redirectAttributes.addFlashAttribute("message", "Critique supprimée avec succès !");
        return "redirect:/web/critiques";
    }

    @GetMapping("/view")
    public String viewCritiques(Model model) {
        model.addAttribute("critiques", critiqueRepository.findAll());
        return "critiques_view";
    }
}
