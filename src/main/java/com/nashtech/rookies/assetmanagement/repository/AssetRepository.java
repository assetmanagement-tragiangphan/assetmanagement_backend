package com.nashtech.rookies.assetmanagement.repository;

import com.nashtech.rookies.assetmanagement.dto.response.AssetHistoryDTO;
import com.nashtech.rookies.assetmanagement.entity.Asset;
import com.nashtech.rookies.assetmanagement.util.StatusConstant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.domain.Page;

@Repository
public interface AssetRepository extends JpaRepository<Asset, Integer>, JpaSpecificationExecutor<Asset> {

    boolean existsByAssetCode(String assetCode);

    Optional<Asset> findAssetByAssetCode(String assetCode);

    boolean existsByAssetCodeAndStatus(String assetCode, StatusConstant statusConstant);

    Optional<Asset> findByAssetCodeAndStatus(String assetCode, StatusConstant statusConstant);

    Optional<Asset> findFirstByOrderByIdDesc(); 
    
    @Query(value = "select new com.nashtech.rookies.assetmanagement.dto.response.AssetHistoryDTO(a.assignedDate, a.assignee.username, a.auditMetadata.createdBy.username, rr.returnedDate) from Assignment a left join fetch ReturnRequest rr on a.id = rr.assignment.id where a.asset.assetCode = ?1")
    public Page<AssetHistoryDTO> findAssetHistory(String requestParams, PageRequest pageRequest);

}
