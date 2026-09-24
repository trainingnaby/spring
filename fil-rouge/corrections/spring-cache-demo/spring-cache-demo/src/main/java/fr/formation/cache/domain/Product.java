package fr.formation.cache.domain;

import java.math.BigDecimal;

public record Product(Long id, String name, BigDecimal price) {
}
