package com.formation.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.formation.domain.Duplicata;
import com.formation.domain.User;
import com.formation.repository.DuplicataRepository;
import com.formation.repository.projection.DuplicataResumeProjection;

import jakarta.annotation.PostConstruct;

@Service
@Transactional
public class DuplicataService {

    private final DuplicataRepository duplicataRepository;
    private final UserService userService;
    private final String cdnUrl;

    public DuplicataService(DuplicataRepository duplicataRepository, UserService userService,
            @Value("${cdn.url}") String cdnUrl) {
        this.duplicataRepository = duplicataRepository;
        this.userService = userService;
        this.cdnUrl = cdnUrl;
    }

    @Transactional(readOnly = true)
    public List<Duplicata> getDuplicatas() {
        return duplicataRepository.findAll(Sort.by(Sort.Direction.DESC, "createdAt"));
    }

    @Transactional(readOnly = true)
    public Optional<Duplicata> findById(String id) {
        return duplicataRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Duplicata getById(String id) {
        return findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Duplicata not found with id: " + id));
    }

    public void deleteById(String id) {
        if (!duplicataRepository.existsById(id)) {
            throw new IllegalArgumentException("Duplicata not found with id: " + id);
        }
        duplicataRepository.deleteById(id);
    }

    public Duplicata createDuplicata(String userId, int montant) {
        String pdfUrl = cdnUrl + "/pdfs/dummy.pdf";

        User user = userService.findById(userId);
        if (user == null) {
            throw new IllegalArgumentException("User not found with id: " + userId);
        }

        Duplicata duplicata = new Duplicata();
        duplicata.setId("dup-" + System.currentTimeMillis());
        duplicata.setUserId(userId);
        duplicata.setMontant(montant);
        duplicata.setPdfUrl(pdfUrl);

        return duplicataRepository.save(duplicata);
    }

    @Transactional(readOnly = true)
    public List<Duplicata> rechercherParUserId(String userId) {
        return duplicataRepository.findByUserId(userId);
    }

    @Transactional(readOnly = true)
    public List<Duplicata> rechercherParMontantEntre(int min, int max) {
        return duplicataRepository.findByMontantBetweenOrderByMontantDesc(min, max);
    }

    @Transactional(readOnly = true)
    public List<Duplicata> rechercherParUserIdContenant(String recherche) {
        return duplicataRepository.findByUserIdContainingIgnoreCase(recherche);
    }

    @Transactional(readOnly = true)
    public List<Duplicata> rechercherAvecJpql(int montantMinimum) {
        return duplicataRepository.rechercherParMontantMinimum(montantMinimum);
    }

    @Transactional(readOnly = true)
    public List<DuplicataResumeProjection> listerResumes(int montantMinimum) {
        return duplicataRepository.findByMontantGreaterThanEqual(montantMinimum);
    }

    @Transactional(readOnly = true)
    public Page<Duplicata> rechercherPagee(String recherche, int page, int size, String sortProperty, String direction) {
        Sort.Direction sortDirection = "asc".equalsIgnoreCase(direction) ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortProperty));
        return duplicataRepository.findByUserIdContainingIgnoreCase(recherche, pageable);
    }

    @PostConstruct
    public void init() {
        System.out.println("DuplicataService initialized with Spring Boot + Spring Data JPA/H2 persistence");
    }
}
