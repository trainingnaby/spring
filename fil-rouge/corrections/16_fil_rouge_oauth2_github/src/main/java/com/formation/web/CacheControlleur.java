package com.formation.web;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.formation.exception.InvalidSearchCriteriaException;
import com.formation.service.DuplicataService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * Endpoints pedagogiques pour inspecter et vider les caches.
 *
 * Ces endpoints sont maintenant proteges par Spring Security.
 * Dans ce TP, seul un utilisateur ADMIN peut consulter ou vider les caches.
 */
@RestController
@RequestMapping("/api/cache")
@Tag(name = "Cache", description = "Endpoints pedagogiques pour consulter et vider les caches Spring")
public class CacheControlleur {

    private final CacheManager cacheManager;

    public CacheControlleur(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    @Operation(summary = "Lister les caches", description = "Retourne les noms des caches connus par le CacheManager.")
    @GetMapping
    public Map<String, Object> listerCaches() {
        Collection<String> cacheNames = cacheManager.getCacheNames();
        Map<String, Object> resultat = new LinkedHashMap<>();
        resultat.put("cacheManager", cacheManager.getClass().getSimpleName());
        resultat.put("caches", cacheNames);
        resultat.put("message", "Appelez plusieurs fois un endpoint de lecture, puis observez les logs SQL : le SQL disparait quand le cache est utilise.");
        return resultat;
    }

    @Operation(summary = "Vider tous les caches", description = "Vide tous les caches connus par le CacheManager.")
    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void viderTousLesCaches() {
        cacheManager.getCacheNames().forEach(this::viderCache);
    }

    @Operation(summary = "Vider un cache", description = "Vide completement le cache dont le nom est passe dans l'URL.")
    @DeleteMapping("/{cacheName}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void viderUnCache(@PathVariable String cacheName) {
        viderCache(cacheName);
    }

    @Operation(summary = "Vider l'entree cachee d'un duplicata", description = "Supprime une seule entree du cache duplicataParId.")
    @DeleteMapping("/duplicata-par-id/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void viderCacheDuplicataParId(@PathVariable String id) {
        Cache cache = getCache(DuplicataService.CACHE_DUPLICATA_PAR_ID);
        cache.evict(id);
    }

    private void viderCache(String cacheName) {
        Cache cache = getCache(cacheName);
        cache.clear();
    }

    private Cache getCache(String cacheName) {
        Cache cache = cacheManager.getCache(cacheName);
        if (cache == null) {
            throw new InvalidSearchCriteriaException("Cache inconnu : " + cacheName
                    + ". Caches disponibles : " + cacheManager.getCacheNames());
        }
        return cache;
    }
}
