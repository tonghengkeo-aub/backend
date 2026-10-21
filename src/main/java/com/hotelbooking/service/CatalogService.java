package com.hotelbooking.service;

import com.hotelbooking.entity.*;
import com.hotelbooking.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CatalogService {

    private final CategoryRepository categoryRepository;
    private final ServiceRepository serviceRepository;
    private final LocationRepository locationRepository;
    private final ProviderRepository providerRepository;

    public CatalogService(CategoryRepository categoryRepository,
                          ServiceRepository serviceRepository,
                          LocationRepository locationRepository,
                          ProviderRepository providerRepository) {
        this.categoryRepository = categoryRepository;
        this.serviceRepository = serviceRepository;
        this.locationRepository = locationRepository;
        this.providerRepository = providerRepository;
    }

    // Categories
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    public Category getCategoryById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Category not found with ID: " + id));
    }

    @Transactional
    public Category saveCategory(Category category) {
        return categoryRepository.save(category);
    }

    // Services
    public List<ServiceEntity> getAllServices() {
        return serviceRepository.findByIsActiveTrue();
    }

    public List<ServiceEntity> getServicesByCategory(Long categoryId) {
        return serviceRepository.findByCategoryIdAndIsActiveTrue(categoryId);
    }

    public List<ServiceEntity> getFeaturedServices() {
        return serviceRepository.findByIsFeaturedTrueAndIsActiveTrue();
    }

    public List<ServiceEntity> getPopularServices() {
        return serviceRepository.findByIsPopularTrueAndIsActiveTrue();
    }

    public List<ServiceEntity> searchServices(String query) {
        if (query == null || query.trim().isEmpty()) {
            return serviceRepository.findByIsActiveTrue();
        }
        return serviceRepository.searchServices(query.trim());
    }

    public ServiceEntity getServiceById(Long id) {
        return serviceRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Service not found with ID: " + id));
    }

    @Transactional
    public ServiceEntity saveService(ServiceEntity service) {
        return serviceRepository.save(service);
    }

    @Transactional
    public void deleteService(Long id) {
        ServiceEntity service = getServiceById(id);
        service.setIsActive(false);
        serviceRepository.save(service);
    }

    // Locations
    public List<Location> getAllLocations() {
        return locationRepository.findAll();
    }

    public Location getLocationById(Long id) {
        return locationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Location not found with ID: " + id));
    }

    @Transactional
    public Location saveLocation(Location location) {
        return locationRepository.save(location);
    }

    // Providers
    public List<Provider> getAllProviders() {
        return providerRepository.findAll();
    }

    public Provider getProviderById(Long id) {
        return providerRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Provider not found with ID: " + id));
    }

    @Transactional
    public Provider saveProvider(Provider provider) {
        return providerRepository.save(provider);
    }
}
