package com.formation.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.formation.domain.Duplicata;
import com.formation.domain.User;
import com.formation.repository.DuplicataRepository;

import jakarta.annotation.PostConstruct;

@Service
//@Scope("prototype") // chaque fois qu'on demande un bean DuplicataService, Spring en crée une nouvelle instance
public class DuplicataService {

	private DuplicataRepository duplicataRepository;
	private final UserService userService;
	private final String cdnUrl;

	// Injection du repository, du UserService et du CDN URL via le constructeur.
	public DuplicataService(DuplicataRepository duplicataRepository, UserService userService,
			@Value("${cdn.url}") String cdnUrl) {
		this.duplicataRepository = duplicataRepository;
		this.userService = userService;
		this.cdnUrl = cdnUrl;
	}

	public List<Duplicata> getDuplicatas() {
		return duplicataRepository.findAll();
	}

	public Optional<Duplicata> findById(String id) {
		return duplicataRepository.findById(id);
	}

	public Duplicata getById(String id) {
		return duplicataRepository.findById(id)
				.orElseThrow(() -> new IllegalArgumentException("Duplicata not found with id: " + id));
	}

	public void deleteById(String id) {
		boolean removed = duplicataRepository.deleteById(id);
		if (!removed) {
			throw new IllegalArgumentException("Duplicata not found with id: " + id);
		}
	}

	public Duplicata createDuplicata(String userId, int montant) {

		// construction de l'url du pdf à partir du CDN URL injecté
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

		//sauvegarde du duplicata en base de données via le repository
		duplicataRepository.save(duplicata);
		return duplicata;
	}

	@PostConstruct
	public void init() {
		System.out.println("DuplicataService initialized with JDBC/H2 persistence");
	}
}
