package com.example.bibliotheque.config;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import com.example.bibliotheque.entity.Auteur;
import com.example.bibliotheque.entity.Categorie;
import com.example.bibliotheque.entity.Critique;
import com.example.bibliotheque.entity.Livre;
import com.example.bibliotheque.jpa.repository.AuteurRepository;
import com.example.bibliotheque.jpa.repository.CategorieRepository;
import com.example.bibliotheque.jpa.repository.CritiqueRepository;
import com.example.bibliotheque.jpa.repository.LivreRepository;
import com.example.bibliotheque.service.LivreService;

@Component
public class DataLoader implements CommandLineRunner {

    private final AuteurRepository auteurRepository;
    private final CategorieRepository categorieRepository;
    private final LivreService livreService;
    private final CritiqueRepository critiqueRepository;

    public DataLoader(AuteurRepository auteurRepository,
                      CategorieRepository categorieRepository,
                      LivreService livreService,
                      CritiqueRepository critiqueRepository) {
        this.auteurRepository = auteurRepository;
        this.categorieRepository = categorieRepository;
        this.livreService = livreService;
        this.critiqueRepository = critiqueRepository;
    }

    @Override
    public void run(String... args) throws Exception {

        Map<String, Auteur> auteurs = new HashMap<>();
        Map<String, Categorie> categories = new HashMap<>();
        Random random = new Random();

        // Lire le CSV depuis resources
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(
                        new ClassPathResource("livres.csv").getInputStream(),
                        StandardCharsets.UTF_8))) {

            String line = br.readLine(); // ignorer header
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                String titre = parts[0].trim();
                String isbn = parts[1].trim();
                LocalDate date = LocalDate.parse(parts[2].trim());
                String auteurNom = parts[3].trim();
                String categorieNom = parts[4].trim();

                Auteur auteur = auteurs.computeIfAbsent(auteurNom, nom -> {
                    Auteur a = new Auteur();
                    a.setNom(nom);
                    return auteurRepository.save(a);
                });

                Categorie categorie = categories.computeIfAbsent(categorieNom, nom -> {
                    Categorie c = new Categorie();
                    c.setNom(nom);
                    return categorieRepository.save(c);
                });

                Livre livre = new Livre();
                livre.setTitre(titre);
                livre.setIsbn(isbn);
                livre.setDatePublication(date);
                livre.setAuteur(auteur);
                livre.setCategorie(categorie);

                Livre savedLivre = livreService.saveLivre(livre);

                int critiqueCount = random.nextInt(4);
                for (int i = 1; i <= critiqueCount; i++) {
                    Critique critique = new Critique();
                    critique.setResume("Critique " + i + " pour " + titre);
                    critique.setScore(1 + random.nextInt(5));
                    critique.setLivre(savedLivre);
                    critiqueRepository.save(critique);
                }
            }
        }
    }
}
