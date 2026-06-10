package com.formation.service;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.formation.domain.Duplicata;
import com.formation.domain.User;

import jakarta.annotation.PostConstruct;

@Service
//@Scope("prototype") // chaque fois qu'on demande un bean DuplicataService, Spring en crée une nouvelle instance
public class DuplicataService {
    
    private List<Duplicata> duplicatas = new CopyOnWriteArrayList<>();
    
    private UserService userService;
    
    private String cdnUrl;
    
    // Injection du UserService et du CDN URL (dynamiquement) via le constructeur
    public DuplicataService(UserService userService, @Value("${cdn.url}") String cdnUrl) {
        this.userService = userService;
        this.cdnUrl = cdnUrl;
    }
    
    public List<Duplicata> getDuplicatas() {
        return duplicatas;
    }

    public Optional<Duplicata> findById(String id) {
        return duplicatas.stream()
                .filter(duplicata -> duplicata.getId().equals(id))
                .findFirst();
    }

    public Duplicata getById(String id) {
        return findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Duplicata not found with id: " + id));
    }

    public void deleteById(String id) {
        boolean removed = duplicatas.removeIf(duplicata -> duplicata.getId().equals(id));
        if (!removed) {
            throw new IllegalArgumentException("Duplicata not found with id: " + id);
        }
    }
    
    public Duplicata createDuplicata(String userId, int montant) {
        
        // construction de l'url du pdf à partir du CDN URL injecté
        String pdfUrl = cdnUrl + "/pdfs/dummy.pdf";
        
        User user = userService.findById(userId);
        if(user == null) {
            throw new IllegalArgumentException("User not found with id: " + userId);
        }
        
        Duplicata duplicata = new Duplicata();
        duplicata.setId("dup-" + System.currentTimeMillis());
        duplicata.setUserId(userId);
        duplicata.setMontant(montant);
        duplicata.setPdfUrl(pdfUrl);
        
        duplicatas.add(duplicata);
        return duplicata;
    }
    
    @PostConstruct // cette méthode sera appelée automatiquement par Spring après l'instanciation du bean et l'injection de ses dépendances
    public void init() {
        System.out.println("DuplicataService initialized with dummy data");
    }

}
