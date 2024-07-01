package com.nashtech.rookies.assetmanagement.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.nashtech.rookies.assetmanagement.entity.Assignment;
import com.nashtech.rookies.assetmanagement.util.StatusConstant;

@Repository
public interface AssignmentRepository extends JpaRepository<Assignment, Integer>, JpaSpecificationExecutor<Assignment> {
    Optional<Assignment> findByIdAndStatusEquals(Integer id, StatusConstant status);

    boolean existsByAssetId(Integer assetId);
}
