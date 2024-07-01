package com.nashtech.rookies.assetmanagement.controller;

import com.nashtech.rookies.assetmanagement.dto.UserDetailsDto;
import com.nashtech.rookies.assetmanagement.dto.request.ReturnAsset.ReturnAssetRequest;
import com.nashtech.rookies.assetmanagement.dto.response.ResponseDto;
import com.nashtech.rookies.assetmanagement.dto.response.ReturnAssetRequestDTO;
import com.nashtech.rookies.assetmanagement.service.ReturnAssetService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/return")
@AllArgsConstructor
public class ReturnAssetController {

    private final ReturnAssetService returnAssetService;

    @PostMapping({"","/"})
    public ResponseEntity<ResponseDto<ReturnAssetRequestDTO>> returnAsset(@RequestBody ReturnAssetRequest request, Authentication authentication) {
        UserDetailsDto requestUser = (UserDetailsDto) authentication.getPrincipal();
        return ResponseEntity.status(HttpStatus.CREATED).body(returnAssetService.createReturnRequest(request, requestUser));
    }
}
