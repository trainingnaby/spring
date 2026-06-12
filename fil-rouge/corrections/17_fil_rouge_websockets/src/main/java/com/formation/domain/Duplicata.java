package com.formation.domain;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

/**
 * Entité JPA représentant un duplicata d'impôts.
 */
@Entity
@Table(name = "duplicata")
@Schema(description = "Duplicata d'impôts généré par l'application")
public class Duplicata {

    @Id
    @Column(length = 64)
    @Schema(description = "Identifiant technique du duplicata", example = "dup-demo-001")
    private String id;

    @Column(name = "user_id", nullable = false, length = 64)
    @Schema(description = "Identifiant fiscal normalisé. L'aspect AOP ajoute FR_ si nécessaire.", example = "FR_123456789")
    private String userId;

    @Column(nullable = false)
    @Schema(description = "Montant associé au duplicata", example = "2500")
    private int montant;

    @Column(name = "pdf_url", nullable = false, length = 500)
    @Schema(description = "URL du PDF généré", example = "https://cdn.prod.impots/pdfs/dummy.pdf")
    private String pdfUrl;

    @Column(name = "created_at", nullable = false)
    @Schema(description = "Date de création du duplicata", example = "2026-06-09T14:30:00")
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public int getMontant() { return montant; }
    public void setMontant(int montant) { this.montant = montant; }
    public String getPdfUrl() { return pdfUrl; }
    public void setPdfUrl(String pdfUrl) { this.pdfUrl = pdfUrl; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    @Override
    public String toString() {
        return "Duplicata [id=" + id + ", userId=" + userId + ", montant=" + montant + ", pdfUrl=" + pdfUrl
                + ", createdAt=" + createdAt + "]";
    }
}
