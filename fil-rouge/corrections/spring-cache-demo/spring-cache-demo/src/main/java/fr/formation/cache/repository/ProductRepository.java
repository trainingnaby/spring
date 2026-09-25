package fr.formation.cache.repository;

import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Repository;

import fr.formation.cache.domain.Product;

@Repository
public class ProductRepository {

    private final Map<Long, Product> products = new ConcurrentHashMap<>();

    public ProductRepository() {
        products.put(1L, new Product(1L, "Clavier professionnel", new BigDecimal("89.90")));
        products.put(2L, new Product(2L, "Écran 27 pouces", new BigDecimal("329.00")));
        products.put(3L, new Product(3L, "Station d'accueil", new BigDecimal("149.50")));
    }

    public Product findById(Long id) {
        simulateSlowAccess();
        Product product = products.get(id);
        if (product == null) {
            throw new IllegalArgumentException("Produit introuvable : " + id);
        }
        return product;
    }

    public Product updatePrice(Long id, BigDecimal price) {
        Product current = findById(id);
        Product updated = new Product(current.id(), current.name(), price);
        products.put(id, updated);
        return updated;
    }

    public void deleteById(Long id) {
        products.remove(id);
    }

    private void simulateSlowAccess() {
        System.out.println("--> Accès à la source de données");
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
