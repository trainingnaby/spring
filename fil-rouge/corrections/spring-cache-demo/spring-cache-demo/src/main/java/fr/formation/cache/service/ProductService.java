package fr.formation.cache.service;

import java.math.BigDecimal;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import fr.formation.cache.domain.Product;
import fr.formation.cache.repository.ProductRepository;

@Service
public class ProductService {

    private final ProductRepository repository;

    public ProductService(ProductRepository repository) {
        this.repository = repository;
    }

    @Cacheable(cacheNames = "products", key = "#id")
    public Product findById(Long id) {
        System.out.println("ProductService.findById(" + id + ") exécuté");
        return repository.findById(id);
    }

    @CachePut(cacheNames = "products", key = "#id")
    public Product updatePrice(Long id, BigDecimal price) {
        System.out.println("Mise à jour du produit " + id);
        return repository.updatePrice(id, price);
    }

    @CacheEvict(cacheNames = "products", key = "#id")
    public void delete(Long id) {
        System.out.println("Suppression du produit " + id + " et éviction du cache");
        repository.deleteById(id);
    }

    @CacheEvict(cacheNames = "products", allEntries = true)
    public void clearCache() {
        System.out.println("Vidage complet du cache products");
    }
}
