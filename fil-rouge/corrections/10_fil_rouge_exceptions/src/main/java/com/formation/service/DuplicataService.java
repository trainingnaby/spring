package com.formation.service;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.formation.domain.Duplicata;
import com.formation.domain.User;
import com.formation.exception.DuplicataNotFoundException;
import com.formation.exception.InvalidSearchCriteriaException;
import com.formation.exception.UserNotFoundException;
import com.formation.repository.DuplicataRepository;
import com.formation.repository.projection.DuplicataResumeProjection;

import jakarta.annotation.PostConstruct;

@Service
@Transactional
public class DuplicataService {

    private static final Set<String> SORT_PROPERTIES_AUTORISEES = Set.of("createdAt", "montant", "userId", "id");

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
                .orElseThrow(() -> new DuplicataNotFoundException(id));
    }

    public void deleteById(String id) {
        if (!duplicataRepository.existsById(id)) {
            throw new DuplicataNotFoundException(id);
        }
        duplicataRepository.deleteById(id);
    }

    public Duplicata createDuplicata(String userId, int montant) {
        String pdfUrl = cdnUrl + "/pdfs/dummy.pdf";

        User user = userService.findById(userId);
        if (user == null) {
            throw new UserNotFoundException(userId);
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
        verifierCriteresPagination(page, size, sortProperty, direction);

        Sort.Direction sortDirection = "asc".equalsIgnoreCase(direction) ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortProperty));
        return duplicataRepository.findByUserIdContainingIgnoreCase(recherche, pageable);
    }

    private void verifierCriteresPagination(int page, int size, String sortProperty, String direction) {
        if (page < 0) {
            throw new InvalidSearchCriteriaException("Le numero de page doit etre superieur ou egal a 0.");
        }
        if (size < 1 || size > 20) {
            throw new InvalidSearchCriteriaException("La taille de page doit etre comprise entre 1 et 20.");
        }
        if (!SORT_PROPERTIES_AUTORISEES.contains(sortProperty)) {
            throw new InvalidSearchCriteriaException("Tri non autorise : " + sortProperty
                    + ". Valeurs autorisees : " + SORT_PROPERTIES_AUTORISEES);
        }
        if (!"asc".equalsIgnoreCase(direction) && !"desc".equalsIgnoreCase(direction)) {
            throw new InvalidSearchCriteriaException("Le sens du tri doit etre asc ou desc.");
        }
    }

    @PostConstruct
    public void init() {
        System.out.println("DuplicataService initialized with Spring Boot + Spring Data JPA/H2 persistence");
    }
}
