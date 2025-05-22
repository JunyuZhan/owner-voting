package com.ownervoting.repository;

import com.ownervoting.model.entity.Owner;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OwnerRepository extends JpaRepository<Owner, Long> {
    Owner findByPhone(String phone);
} 