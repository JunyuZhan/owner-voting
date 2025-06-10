package com.ownervoting.repository;

import com.ownervoting.model.entity.CommunityAdminApplication;
import com.ownervoting.model.entity.CommunityAdminApplication.ApplicationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommunityAdminApplicationRepository extends JpaRepository<CommunityAdminApplication, Long> {

    /**
     * 根据申请状态查找申请
     */
    List<CommunityAdminApplication> findByStatusOrderByCreatedAtDesc(ApplicationStatus status);

    /**
     * 根据申请人手机号查找申请
     */
    List<CommunityAdminApplication> findByApplicantPhoneOrderByCreatedAtDesc(String applicantPhone);

    /**
     * 查找待审核的申请
     */
    @Query("SELECT a FROM CommunityAdminApplication a WHERE a.status = 'PENDING' ORDER BY a.createdAt ASC")
    List<CommunityAdminApplication> findPendingApplications();

    /**
     * 根据小区名称查找申请（避免重复申请相同小区）
     */
    Optional<CommunityAdminApplication> findByCommunityNameAndStatus(String communityName, ApplicationStatus status);

    /**
     * 统计各状态的申请数量
     */
    @Query("SELECT COUNT(a) FROM CommunityAdminApplication a WHERE a.status = :status")
    Long countByStatus(@Param("status") ApplicationStatus status);
} 