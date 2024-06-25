package com.nashtech.rookies.assetmanagement.repository;

import com.nashtech.rookies.assetmanagement.entity.Asset;
import com.nashtech.rookies.assetmanagement.entity.Assignment;
import com.nashtech.rookies.assetmanagement.util.StatusConstant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AssignmentRepository extends JpaRepository<Assignment, Integer> {
    boolean existsById(Integer id);
    Optional<Assignment> findByIdAndStatusEquals(Integer id, StatusConstant status);
}
