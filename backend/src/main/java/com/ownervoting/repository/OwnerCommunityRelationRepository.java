package com.ownervoting.repository;

import com.ownervoting.model.entity.OwnerCommunityRelation;
import com.ownervoting.model.entity.OwnerCommunityRelation.ApplicationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OwnerCommunityRelationRepository extends JpaRepository<OwnerCommunityRelation, Long> {

    /**
     * 查找业主在指定小区的关联关系
     */
    Optional<OwnerCommunityRelation> findByOwnerIdAndCommunityId(Long ownerId, Long communityId);

    /**
     * 查找业主的所有小区关联（已通过的）
     */
    List<OwnerCommunityRelation> findByOwnerIdAndStatus(Long ownerId, ApplicationStatus status);

    /**
     * 查找小区的待审核申请
     */
    @Query("SELECT r FROM OwnerCommunityRelation r WHERE r.community.id = :communityId AND r.status = 'PENDING' ORDER BY r.appliedAt ASC")
    List<OwnerCommunityRelation> findPendingApplicationsByCommunity(@Param("communityId") Long communityId);

    /**
     * 查找小区的所有业主关联
     */
    List<OwnerCommunityRelation> findByCommunityIdAndStatus(Long communityId, ApplicationStatus status);

    /**
     * 统计小区业主数量
     */
    @Query("SELECT COUNT(r) FROM OwnerCommunityRelation r WHERE r.community.id = :communityId AND r.status = 'APPROVED'")
    Long countApprovedOwnersByCommunity(@Param("communityId") Long communityId);
} 