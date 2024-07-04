/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.nashtech.rookies.assetmanagement.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.nashtech.rookies.assetmanagement.dto.response.Report.ReportResponse;
import com.nashtech.rookies.assetmanagement.entity.Asset;

/**
 *
 * @author HP
 */
public interface ReportRepository extends JpaRepository<Asset, Integer>, JpaSpecificationExecutor<Asset> {

    @Query("SELECT new com.nashtech.rookies.assetmanagement.dto.response.Report.ReportResponse(" +
           "c.name AS category, " +
           "COUNT(a.id) AS total, " +
           "COUNT(CASE WHEN a.status = 'ASSIGNED' THEN 1 END) AS assigned, " +
           "COUNT(CASE WHEN a.status = 'AVAILABLE' THEN 1 END) AS available, " +
           "COUNT(CASE WHEN a.status = 'NOT_AVAILABLE' THEN 1 END) AS notAvailable, " +
           "COUNT(CASE WHEN a.status = 'WAITING_FOR_RECYCLING' THEN 1 END) AS waitingForRecycling, " +
           "COUNT(CASE WHEN a.status = 'RECYCLED' THEN 1 END) AS recycled) " +
           "FROM Category c LEFT JOIN c.assets a ON a.auditMetadata.createdBy.id = :id " +
           "GROUP BY c.name")
    Page<ReportResponse> reportStatistic(@Param("id") Integer id, Pageable pageable);

    @Query("SELECT new com.nashtech.rookies.assetmanagement.dto.response.Report.ReportResponse(" +
           "c.name AS category, " +
           "COUNT(a.id) AS total, " +
           "COUNT(CASE WHEN a.status = 'ASSIGNED' THEN 1 END) AS assigned, " +
           "COUNT(CASE WHEN a.status = 'AVAILABLE' THEN 1 END) AS available, " +
           "COUNT(CASE WHEN a.status = 'NOT_AVAILABLE' THEN 1 END) AS notAvailable, " +
           "COUNT(CASE WHEN a.status = 'WAITING_FOR_RECYCLING' THEN 1 END) AS waitingForRecycling, " +
           "COUNT(CASE WHEN a.status = 'RECYCLED' THEN 1 END) AS recycled) " +
           "FROM Category c LEFT JOIN c.assets a ON a.auditMetadata.createdBy.id = :id " +
           "GROUP BY c.name")
    List<ReportResponse> exportReportStatistic(@Param("id") Integer id);
}
