package com.ownervoting.repository;

import com.ownervoting.model.entity.House;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HouseRepository extends JpaRepository<House, Long> {
} 