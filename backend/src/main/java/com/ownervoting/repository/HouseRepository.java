package com.ownervoting.repository;

import com.ownervoting.model.entity.House;
import com.ownervoting.model.entity.House.VerificationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HouseRepository extends JpaRepository<House, Long> {

    /**
     * 根据业主ID查找房屋列表
     */
    List<House> findByOwnerId(Long ownerId);

    /**
     * 查找指定位置的房屋（用于检测重复）
     */
    @Query("SELECT h FROM House h WHERE h.community.id = :communityId " +
           "AND h.building = :building AND h.unit = :unit AND h.room = :room")
    List<House> findByLocation(@Param("communityId") Long communityId,
                              @Param("building") String building,
                              @Param("unit") String unit,
                              @Param("room") String room);

    /**
     * 查找同一位置已认证的房屋
     */
    @Query("SELECT h FROM House h WHERE h.community.id = :communityId " +
           "AND h.building = :building AND h.unit = :unit AND h.room = :room " +
           "AND h.verificationStatus = 'APPROVED'")
    Optional<House> findApprovedByLocation(@Param("communityId") Long communityId,
                                         @Param("building") String building,
                                         @Param("unit") String unit,
                                         @Param("room") String room);

    /**
     * 查找存在争议的房屋
     */
    List<House> findByHasDisputeTrue();

    /**
     * 根据认证状态查找房屋
     */
    List<House> findByVerificationStatus(VerificationStatus status);

    /**
     * 根据小区ID和认证状态查找房屋
     */
    List<House> findByCommunityIdAndVerificationStatus(Long communityId, VerificationStatus status);

    /**
     * 查找待审核的房屋
     */
    @Query("SELECT h FROM House h WHERE h.verificationStatus = 'PENDING' ORDER BY h.createdAt ASC")
    List<House> findPendingHouses();

    /**
     * 根据房产证号查找房屋
     */
    Optional<House> findByCertificateNumber(String certificateNumber);

    /**
     * 查找业主的主要房屋
     */
    Optional<House> findByOwnerIdAndIsPrimaryTrue(Long ownerId);
}