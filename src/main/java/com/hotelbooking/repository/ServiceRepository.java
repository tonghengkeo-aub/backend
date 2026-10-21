package com.hotelbooking.repository;

import com.hotelbooking.entity.ServiceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ServiceRepository extends JpaRepository<ServiceEntity, Long> {
    List<ServiceEntity> findByIsActiveTrue();
    List<ServiceEntity> findByCategoryIdAndIsActiveTrue(Long categoryId);
    List<ServiceEntity> findByIsFeaturedTrueAndIsActiveTrue();
    List<ServiceEntity> findByIsPopularTrueAndIsActiveTrue();

    @Query("SELECT s FROM ServiceEntity s WHERE s.isActive = true AND " +
           "(LOWER(s.name) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(s.description) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(s.location.province) LIKE LOWER(CONCAT('%', :query, '%')))")
    List<ServiceEntity> searchServices(@Param("query") String query);
}
