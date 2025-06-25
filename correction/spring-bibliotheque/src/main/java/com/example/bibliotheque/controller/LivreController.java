
package com.example.bibliotheque.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.bibliotheque.entity.Livre;
import com.example.bibliotheque.jpa.repository.CritiqueRepository;
import com.example.bibliotheque.service.LivreService;

@RestController
@RequestMapping("/api/livres")
public class LivreController {

    @Autowired
    private LivreService livreService;
    
    @Autowired
    private CritiqueRepository critiqueRepository;

    @GetMapping
    public List<Livre> getAll() {
        return livreService.getAllLivres();
    }

    @PostMapping
    public Livre create(@RequestBody Livre livre) {
        return livreService.saveLivre(livre);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        livreService.deleteLivre(id);
        return ResponseEntity.ok().build();
    }
    

    @PostMapping("/{id}/archiver")
    public Livre archiver(@PathVariable Long id) {
        return livreService.archiverLivre(id);
    }

    @GetMapping("/search")
    public List<Livre> search(@RequestParam String keyword) {
        return livreService.search(keyword);
    }
    
    @GetMapping("/edit/{id}")
    public String editLivre(@PathVariable Long id, Model model) {
        Livre livre = livreService.getLivreById(id);
        model.addAttribute("livre", livre);
        return "livre_form";
    }

}
