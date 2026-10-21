package com.hotelbooking.repository;

import com.hotelbooking.entity.HotelSetting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HotelSettingRepository extends JpaRepository<HotelSetting, Long> {
}
