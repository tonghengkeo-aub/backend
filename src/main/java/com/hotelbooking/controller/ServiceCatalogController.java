package com.hotelbooking.controller;

import com.hotelbooking.entity.Category;
import com.hotelbooking.entity.Location;
import com.hotelbooking.entity.Provider;
import com.hotelbooking.entity.ServiceEntity;
import com.hotelbooking.service.CatalogService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "*")
public class ServiceCatalogController {

    private final CatalogService catalogService;

    public ServiceCatalogController(CatalogService catalogService) {
        this.catalogService = catalogService;
    }

    // SERVICES
    @GetMapping("/api/services")
    public ResponseEntity<List<ServiceEntity>> getServices(
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Boolean featured,
            @RequestParam(required = false) Boolean popular) {
        if (categoryId != null) {
            return ResponseEntity.ok(catalogService.getServicesByCategory(categoryId));
        }
        if (Boolean.TRUE.equals(featured)) {
            return ResponseEntity.ok(catalogService.getFeaturedServices());
        }
        if (Boolean.TRUE.equals(popular)) {
            return ResponseEntity.ok(catalogService.getPopularServices());
        }
        return ResponseEntity.ok(catalogService.getAllServices());
    }

    @GetMapping("/api/services/{id}")
    public ResponseEntity<ServiceEntity> getServiceById(@PathVariable Long id) {
        return ResponseEntity.ok(catalogService.getServiceById(id));
    }

    @GetMapping("/api/services/search")
    public ResponseEntity<List<ServiceEntity>> searchServices(@RequestParam(required = false, defaultValue = "") String q) {
        return ResponseEntity.ok(catalogService.searchServices(q));
    }

    // CATEGORIES
    @GetMapping("/api/categories")
    public ResponseEntity<List<Category>> getCategories() {
        return ResponseEntity.ok(catalogService.getAllCategories());
    }

    @GetMapping("/api/categories/{id}")
    public ResponseEntity<Category> getCategoryById(@PathVariable Long id) {
        return ResponseEntity.ok(catalogService.getCategoryById(id));
    }

    // LOCATIONS
    @GetMapping("/api/locations")
    public ResponseEntity<List<Location>> getLocations() {
        return ResponseEntity.ok(catalogService.getAllLocations());
    }

    @GetMapping("/api/locations/{id}")
    public ResponseEntity<Location> getLocationById(@PathVariable Long id) {
        return ResponseEntity.ok(catalogService.getLocationById(id));
    }

    // PROVIDERS
    @GetMapping("/api/providers")
    public ResponseEntity<List<Provider>> getProviders() {
        return ResponseEntity.ok(catalogService.getAllProviders());
    }

    @GetMapping("/api/providers/{id}")
    public ResponseEntity<Provider> getProviderById(@PathVariable Long id) {
        return ResponseEntity.ok(catalogService.getProviderById(id));
    }
}
