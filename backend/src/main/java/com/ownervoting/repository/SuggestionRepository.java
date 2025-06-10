package com.ownervoting.repository;

import com.ownervoting.model.entity.Suggestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SuggestionRepository extends JpaRepository<Suggestion, Long> {
    
    @Query("SELECT s FROM Suggestion s WHERE s.owner.id = :ownerId")
    List<Suggestion> findByOwnerId(@Param("ownerId") Long ownerId);
    
    @Override
    @Query("SELECT s FROM Suggestion s WHERE s.id = :id")
    Optional<Suggestion> findById(@Param("id") Long id);
}