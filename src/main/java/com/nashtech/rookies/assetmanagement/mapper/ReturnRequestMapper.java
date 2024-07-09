/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.nashtech.rookies.assetmanagement.mapper;

import com.nashtech.rookies.assetmanagement.dto.response.ReturnRequestResponseDTO;
import com.nashtech.rookies.assetmanagement.entity.ReturnRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 *
 * @author HP
 * @author Tamina
 */
@Mapper
public interface ReturnRequestMapper {

    @Mapping(target = "id", source = "returnRequest.id")
    @Mapping(target = "assetCode", source = "returnRequest.assignment.asset.assetCode")
    @Mapping(target = "assetName", source = "returnRequest.assignment.asset.name")
    @Mapping(target = "requestedBy", source = "returnRequest.requestedBy.username")
    @Mapping(target = "assignedDate", source = "returnRequest.assignment.assignedDate")
    @Mapping(target = "acceptedBy", source = "returnRequest.acceptedBy.username")
    @Mapping(target = "returnedDate", source = "returnRequest.returnedDate")
    @Mapping(target = "state", source = "returnRequest.status")
    ReturnRequestResponseDTO entityToDto(ReturnRequest returnRequest);

}
