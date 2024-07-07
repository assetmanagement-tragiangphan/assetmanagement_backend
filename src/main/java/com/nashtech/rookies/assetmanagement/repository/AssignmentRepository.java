package com.nashtech.rookies.assetmanagement.repository;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.nashtech.rookies.assetmanagement.dto.response.AssignmentDetailResponse;
import com.nashtech.rookies.assetmanagement.entity.Assignment;
import com.nashtech.rookies.assetmanagement.util.LocationConstant;
import com.nashtech.rookies.assetmanagement.util.StatusConstant;

@Repository
public interface AssignmentRepository extends JpaRepository<Assignment, Integer>, JpaSpecificationExecutor<Assignment> {
    Optional<Assignment> findByIdAndStatusEquals(Integer id, StatusConstant status);

    boolean existsByAssetId(Integer assetId);

    @Query(value = "SELECT new com.nashtech.rookies.assetmanagement.dto.response.AssignmentDetailResponse("
            + "a.id, "
            + "a.asset.assetCode, "
            + "a.asset.name, "
            + "a.asset.category.name, "
            + "a.asset.specification, "
            + "a.assignee.username, "
            + "a.auditMetadata.createdBy.username, "
            + "a.assignedDate, "
            + "a.status, "
            + "a.note) "
            + "FROM Assignment a"
            + " WHERE (:search IS NULL OR LOWER(a.asset.assetCode) LIKE concat('%',LOWER(:search),'%') OR LOWER(a.asset.name) LIKE concat('%',LOWER(:search),'%') OR LOWER(a.assignee.username) LIKE concat('%',LOWER(:search),'%'))"
            + " AND (:status IS NULL OR a.status IN :status)"
            + " AND (COALESCE(:assignedDate,NULL) IS NULL OR a.assignedDate = :assignedDate)"
            + " AND (:location IS NULL OR a.auditMetadata.createdBy.location = :location)")
    Page<AssignmentDetailResponse> findAllAssignmentDetails(@Param("search") String search,
                                                            @Param("status") List<String> status,
                                                            @Param("assignedDate") LocalDate assignedDate,
                                                            @Param("location") LocationConstant location,
                                                            Pageable pageable);

    @Query(value = "SELECT new com.nashtech.rookies.assetmanagement.dto.response.AssignmentDetailResponse("
            + "a.id, "
            + "a.asset.assetCode, "
            + "a.asset.name, "
            + "a.asset.category.name, "
            + "a.asset.specification, "
            + "a.assignee.username, "
            + "a.auditMetadata.createdBy.username, "
            + "a.assignedDate, "
            + "a.status, "
            + "a.note) "
            + "FROM Assignment a"
            + " WHERE (LOWER(a.assignee.username) LIKE LOWER(:username))"
            + " AND (COALESCE(:toAssignedDate, NULL) IS NULL OR a.assignedDate <= :toAssignedDate)"
            + " AND (:status IS NULL OR a.status IN :status)")
    Page<AssignmentDetailResponse> findOwnAssignmentDetails(@Param("username") String username,
                                                            @Param("toAssignedDate") LocalDate toAssignedDate,
                                                            @Param("status") List<String> status,
                                                            Pageable pageable);
}
