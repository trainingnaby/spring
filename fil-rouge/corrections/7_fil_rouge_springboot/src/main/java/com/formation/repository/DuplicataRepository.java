package com.formation.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.formation.domain.Duplicata;

/**
 * Repository JDBC volontairement simple pour le TP.
 *
 * Objectif pédagogique : montrer ce que Spring Boot/JPA masquent souvent :
 * - déclaration du DataSource,
 * - création du JdbcTemplate,
 * - initialisation du schéma,
 * - mapping manuel entre ResultSet et objet Java.
 */
@Repository
public class DuplicataRepository {

    private static final RowMapper<Duplicata> DUPLICATA_ROW_MAPPER = (rs, rowNum) -> {
        Duplicata duplicata = new Duplicata();
        duplicata.setId(rs.getString("id"));
        duplicata.setUserId(rs.getString("user_id"));
        duplicata.setMontant(rs.getInt("montant"));
        duplicata.setPdfUrl(rs.getString("pdf_url"));
        return duplicata;
    };

    private final JdbcTemplate jdbcTemplate;

    public DuplicataRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Duplicata> findAll() {
        return jdbcTemplate.query(
                "select id, user_id, montant, pdf_url from duplicata order by created_at desc",
                DUPLICATA_ROW_MAPPER);
    }

    public Optional<Duplicata> findById(String id) {
        List<Duplicata> result = jdbcTemplate.query(
                "select id, user_id, montant, pdf_url from duplicata where id = ?",
                DUPLICATA_ROW_MAPPER,
                id);
        return result.stream().findFirst();
    }

    public void save(Duplicata duplicata) {
        jdbcTemplate.update(
                "insert into duplicata(id, user_id, montant, pdf_url) values (?, ?, ?, ?)",
                duplicata.getId(),
                duplicata.getUserId(),
                duplicata.getMontant(),
                duplicata.getPdfUrl());
    }

    public boolean deleteById(String id) {
        int rows = jdbcTemplate.update("delete from duplicata where id = ?", id);
        return rows > 0;
    }
}
