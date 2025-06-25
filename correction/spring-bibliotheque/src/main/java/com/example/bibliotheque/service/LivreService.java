
package com.example.bibliotheque.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.bibliotheque.config.BibliothequeProperties;
import com.example.bibliotheque.dto.LivreDTO;
import com.example.bibliotheque.elasticsearch.repository.LivreSearchRepository;
import com.example.bibliotheque.entity.Livre;
import com.example.bibliotheque.jpa.repository.CritiqueRepository;
import com.example.bibliotheque.jpa.repository.LivreRepository;

@Service
public class LivreService {

    private final LivreRepository livreRepository;
    private final LivreSearchRepository livreSearchRepository;
    private final BibliothequeProperties bibliothequeProperties;
    private final Environment environment;
    
    private final CritiqueRepository critiqueRepository;

    @Value("${bibliotheque.reading.avg-time}")
    private int avgReadingTime;

    public LivreService(LivreRepository livreRepository,
                        LivreSearchRepository livreSearchRepository,
                        BibliothequeProperties bibliothequeProperties,
                        Environment environment, CritiqueRepository critiqueRepository) {
        this.livreRepository = livreRepository;
        this.livreSearchRepository = livreSearchRepository;
        this.bibliothequeProperties = bibliothequeProperties;
        this.environment = environment;
        this.critiqueRepository = critiqueRepository;
    }

    public Livre getLivreById(Long id) {
        return livreRepository.findById(id).orElseThrow(() -> new RuntimeException("Livre introuvable"));
    }

    @Cacheable("livres")
    public List<Livre> getAllLivres() {
        return livreRepository.findAll();
    }

    public List<LivreDTO> getAllLivresDTO() {
        return livreRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private LivreDTO convertToDTO(Livre livre) {
        LivreDTO dto = new LivreDTO();
        dto.setId(livre.getId());
        dto.setTitre(livre.getTitre());
        dto.setIsbn(livre.getIsbn());
        dto.setDatePublication(livre.getDatePublication());
        return dto;
    }

    @CachePut("livres")
    public Livre saveLivre(Livre livre) {
    	Optional<Livre> existingLivre = livreRepository.findByIsbn(livre.getIsbn());

    	if (existingLivre.isPresent()) {
    	    if (livre.getId() == null || !existingLivre.get().getId().equals(livre.getId())) {
    	        throw new RuntimeException("Un livre avec cet ISBN existe déjà.");
    	    }
    	}
        Livre saved = livreRepository.save(livre);
        livreSearchRepository.save(saved);
        return saved;
    }

    @CacheEvict(value = "livres", allEntries = true)
    public void deleteLivre(Long id) {
        Livre livre = getLivreById(id);
        if (livre.isArchive()) {
            throw new RuntimeException("Impossible de supprimer un livre archivé.");
        }
        critiqueRepository.deleteAll(critiqueRepository.findByLivreId(id));
        livreRepository.deleteById(id);
        livreSearchRepository.deleteById(id);
    }

    @Transactional
    @CacheEvict(value = "livres", allEntries = true)
    public Livre archiverLivre(Long id) {
        Livre livre = getLivreById(id);
        if (livre.isEmprunte()) {
            throw new RuntimeException("Impossible d'archiver un livre emprunté");
        }
        livre.setArchive(true);
        return livreRepository.save(livre);
    }

    public List<Livre> search(String keyword) {
        Iterable<Livre> results = livreSearchRepository.findByTitreContaining(keyword);
        return (List<Livre>) results;
    }

    public int getAvgReadingTimeFromProperties() {
        return bibliothequeProperties.getReadingAvgTime();
    }

    public String getEnvPropertyExample() {
        return environment.getProperty("spring.datasource.url");
    }
    public List<Livre> getLivresOrderByAuteur() {
        return livreRepository.findAllByOrderByAuteurNomAsc();
    }

    public List<Livre> getLivresOrderByDate() {
        return livreRepository.findAllByOrderByDatePublicationAsc();
    }

    public List<Livre> getLivresOrderByCategorie() {
        return livreRepository.findAllByOrderByCategorieNomAsc();
    }

}
