package com.formation.mvc;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.formation.dto.DuplicataDto;
import com.formation.service.DuplicataService;

import jakarta.validation.Valid;

@Controller
public class DuplicataMvcController {

    private final DuplicataService duplicataService;

    public DuplicataMvcController(DuplicataService duplicataService) {
        this.duplicataService = duplicataService;
    }

    @GetMapping("/ui/duplicatas")
    public String listerDuplicatas(Model model) {
        model.addAttribute("duplicatas", duplicataService.getDuplicatas());
        return "duplicatas/list";
    }

    @GetMapping("/ui/duplicatas/new")
    public String afficherFormulaireCreation(Model model) {
        model.addAttribute("duplicataDto", new DuplicataDto());
        return "duplicatas/form";
    }

    @PostMapping("/ui/duplicatas")
    public String creerDuplicata(@Valid @ModelAttribute("duplicataDto") DuplicataDto duplicataDto,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            return "duplicatas/form";
        }

        try {
            duplicataService.createDuplicata(duplicataDto.getUserId(), duplicataDto.getMontant());
            redirectAttributes.addFlashAttribute("message", "Le duplicata a été généré avec succès.");
            return "redirect:/ui/duplicatas";
        } catch (IllegalArgumentException exception) {
            bindingResult.reject("duplicata.creation", exception.getMessage());
            return "duplicatas/form";
        }
    }

    @GetMapping("/ui/duplicatas/{id}")
    public String afficherDetail(@PathVariable String id, Model model, RedirectAttributes redirectAttributes) {
        try {
            model.addAttribute("duplicata", duplicataService.getById(id));
            return "duplicatas/detail";
        } catch (IllegalArgumentException exception) {
            redirectAttributes.addFlashAttribute("errorMessage", "Le duplicata demandé est introuvable.");
            return "redirect:/ui/duplicatas";
        }
    }

    @PostMapping("/ui/duplicatas/{id}/delete")
    public String supprimerDuplicata(@PathVariable String id, RedirectAttributes redirectAttributes) {
        try {
            duplicataService.deleteById(id);
            redirectAttributes.addFlashAttribute("message", "Le duplicata a été supprimé avec succès.");
        } catch (IllegalArgumentException exception) {
            redirectAttributes.addFlashAttribute("errorMessage", "Le duplicata à supprimer est introuvable.");
        }
        return "redirect:/ui/duplicatas";
    }
}
