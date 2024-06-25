/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.nashtech.rookies.assetmanagement.service;

import com.nashtech.rookies.assetmanagement.dto.UserDetailsDto;
import com.nashtech.rookies.assetmanagement.dto.request.Asset.AssetRequestDTO;
import com.nashtech.rookies.assetmanagement.dto.request.Asset.CreateAssetRequest;
import com.nashtech.rookies.assetmanagement.dto.request.Asset.EditAssetRequest;
import com.nashtech.rookies.assetmanagement.dto.response.AssetResponseDto;
import com.nashtech.rookies.assetmanagement.dto.response.ResponseDto;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetailsService;

/**
 *
 * @author HP
 */
public interface AssetService {

    ResponseDto getAll(AssetRequestDTO requestParams, Pageable pageable, UserDetailsDto requestUser);
    ResponseDto getAll();

    ResponseDto<AssetResponseDto> saveAsset(CreateAssetRequest request, UserDetailsDto requestUser);
    ResponseDto<AssetResponseDto> editAsset(String assetCode, EditAssetRequest request);
}
