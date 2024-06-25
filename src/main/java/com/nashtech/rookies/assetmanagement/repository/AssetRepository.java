package com.nashtech.rookies.assetmanagement.repository;

import com.nashtech.rookies.assetmanagement.entity.Asset;
import com.nashtech.rookies.assetmanagement.util.StatusConstant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AssetRepository extends JpaRepository<Asset, Integer>, JpaSpecificationExecutor<Asset> {
    boolean existsByAssetCode(String assetCode);
    Optional<Asset> findAssetByAssetCode(String assetCode);
    Optional<Asset> findAssetByAssetCodeAndStatus(String assetCode, StatusConstant statusConstant);
    Optional<Asset> findFirstByOrderByIdDesc();
}
