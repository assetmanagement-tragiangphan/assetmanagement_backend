/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.nashtech.rookies.assetmanagement.service;

import com.nashtech.rookies.assetmanagement.dto.UserDetailsDto;
import com.nashtech.rookies.assetmanagement.dto.request.ReturnRequest.ReturnRequestRequestDTO;
import com.nashtech.rookies.assetmanagement.dto.response.ResponseDto;
import org.springframework.data.domain.Pageable;

/**
 *
 * @author HP
 */
public interface ReturnRequestService {

    ResponseDto getAll(ReturnRequestRequestDTO requestParams, Pageable pageable, UserDetailsDto requestUser);

    ResponseDto cancelOne(Integer id, UserDetailsDto requestUser);

}
