package com.ownervoting.repository;

import com.ownervoting.model.entity.AdminUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface AdminUserRepository extends JpaRepository<AdminUser, Long> {
    
    @Query("SELECT a FROM AdminUser a WHERE a.username = :username")
    Optional<AdminUser> findByUsername(@Param("username") String username);
    
    @Override
    @Query("SELECT a FROM AdminUser a WHERE a.id = :id")
    Optional<AdminUser> findById(@Param("id") Long id);
}