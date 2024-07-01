package com.nashtech.rookies.assetmanagement.service;

import com.nashtech.rookies.assetmanagement.dto.UserDetailsDto;
import com.nashtech.rookies.assetmanagement.dto.request.ReturnAsset.ReturnAssetRequest;
import com.nashtech.rookies.assetmanagement.dto.response.ResponseDto;
import com.nashtech.rookies.assetmanagement.dto.response.ReturnAssetRequestDTO;

public interface ReturnAssetService {
    ResponseDto<ReturnAssetRequestDTO> createReturnRequest(ReturnAssetRequest request, UserDetailsDto requestUser);
}
