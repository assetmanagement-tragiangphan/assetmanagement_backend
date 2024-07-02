package com.nashtech.rookies.assetmanagement.repository;

import com.nashtech.rookies.assetmanagement.entity.ReturnRequest;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;


@Repository
public interface ReturnRequestRepository extends JpaRepository<ReturnRequest, Integer>, JpaSpecificationExecutor<ReturnRequest> {
    Optional<ReturnRequest> findByAssignmentId(int assignmentId);
}
