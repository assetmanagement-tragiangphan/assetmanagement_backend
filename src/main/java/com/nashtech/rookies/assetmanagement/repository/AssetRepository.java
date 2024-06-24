package com.nashtech.rookies.assetmanagement.repository;

import com.nashtech.rookies.assetmanagement.entity.Asset;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AssetRepository extends JpaRepository<Asset, Integer>, JpaSpecificationExecutor<Asset> {
    Optional<Asset> findAssetByAssetCode(String assetCode);
    Optional<Asset> findFirstByOrderByIdDesc();
}
