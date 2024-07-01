package com.nashtech.rookies.assetmanagement.mapper;

import com.nashtech.rookies.assetmanagement.dto.UserDetailsDto;
import com.nashtech.rookies.assetmanagement.dto.response.AssignmentResponse;
import com.nashtech.rookies.assetmanagement.entity.Asset;
import com.nashtech.rookies.assetmanagement.entity.Assignment;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.nashtech.rookies.assetmanagement.dto.response.AssignmentDetailResponse;
import com.nashtech.rookies.assetmanagement.entity.User;

@Mapper
public interface AssignmentMapper {
    @Mapping(target = "assetCode", source = "assignment.asset.assetCode")
    @Mapping(target = "assetName", source = "assignment.asset.name")
    @Mapping(target = "assignBy", source = "requestUser.username")
    @Mapping(target = "assignTo", source = "assignment.assignee.username")
    @Mapping(target = "assignedDate", source = "assignment.assignedDate")
    @Mapping(target = "status", source = "assignment.status")
    @Mapping(target = "id", source = "assignment.id")
    AssignmentResponse entityToDto(Assignment assignment, UserDetailsDto requestUser);

    @Mapping(target = "assetCode", source = "assignment.asset.assetCode")
    @Mapping(target = "assetName", source = "assignment.asset.name")
    @Mapping(target = "assignedBy", source = "user.username")
    @Mapping(target = "assignedTo", source = "assignment.assignee.username")
    @Mapping(target = "assignedDate", source = "assignment.assignedDate")
    @Mapping(target = "status", source = "assignment.status")
    @Mapping(target = "category", source = "asset.category.name")
    @Mapping(target = "specification", source = "asset.specification")
    AssignmentDetailResponse entityToDetailDto(Assignment assignment, User user, Asset asset);
}
