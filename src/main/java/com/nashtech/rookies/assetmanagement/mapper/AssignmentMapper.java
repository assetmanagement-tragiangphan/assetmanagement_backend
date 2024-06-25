package com.nashtech.rookies.assetmanagement.mapper;

import com.nashtech.rookies.assetmanagement.dto.UserDetailsDto;
import com.nashtech.rookies.assetmanagement.dto.response.AssetResponseDto;
import com.nashtech.rookies.assetmanagement.dto.response.AssignmentResponse;
import com.nashtech.rookies.assetmanagement.entity.Asset;
import com.nashtech.rookies.assetmanagement.entity.Assignment;
import com.nashtech.rookies.assetmanagement.util.StatusConstant;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.time.LocalDate;

@Mapper
public interface AssignmentMapper {
    @Mapping(target = "assetCode", source = "assignment.asset.assetCode")
    @Mapping(target = "assetName", source = "assignment.asset.name")
    @Mapping(target = "assignBy", source = "requestUser.username")
    @Mapping(target = "assignTo", source = "assignment.assignee.username")
    @Mapping(target = "assignedDate", source = "assignment.assignedDate")
    @Mapping(target = "status", source = "assignment.status")
    AssignmentResponse entityToDto(Assignment assignment, UserDetailsDto requestUser);
}
