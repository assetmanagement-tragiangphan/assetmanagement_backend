package com.nashtech.rookies.assetmanagement.mapper;

import com.nashtech.rookies.assetmanagement.dto.response.ReturnAssetRequestDTO;
import com.nashtech.rookies.assetmanagement.entity.ReturnRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface ReturnAssetMapper {

    @Mapping(target = "assetCode", source = "entity.assignment.asset.assetCode")
    @Mapping(target = "assetName", source = "entity.assignment.asset.name")
    @Mapping(target = "requestedBy", source = "entity.requestedBy.username")
    @Mapping(target = "assignedDate", source = "entity.assignment.assignedDate")
    @Mapping(target = "acceptedBy", source = "entity.acceptedBy.username")
    @Mapping(target = "state", source = "entity.status")
    ReturnAssetRequestDTO entityToDto(ReturnRequest entity);
}
