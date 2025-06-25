
package com.example.bibliotheque.controller;

import com.example.bibliotheque.entity.Critique;
import com.example.bibliotheque.jpa.repository.CritiqueRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/critiques")
public class CritiqueController {

    @Autowired
    private CritiqueRepository critiqueRepository;

    @GetMapping
    public List<Critique> getAll() {
        return critiqueRepository.findAll();
    }

    @PostMapping
    public Critique create(@RequestBody Critique critique) {
        return critiqueRepository.save(critique);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        critiqueRepository.deleteById(id);
    }
}
