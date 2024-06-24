/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.nashtech.rookies.assetmanagement.mapper;

import com.nashtech.rookies.assetmanagement.dto.response.AssetResponseDto;
import com.nashtech.rookies.assetmanagement.entity.Asset;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 *
 * @author HP
 * @author Tamina
 */
@Mapper
public interface AssetMapper {
    
    @Mapping(target = "state", source = "asset.status")
    @Mapping(target = "category", source = "asset.category.name")
    AssetResponseDto entityToDto(Asset asset);
    
}
