/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.nashtech.rookies.assetmanagement.controller;

import com.nashtech.rookies.assetmanagement.dto.UserDetailsDto;
import com.nashtech.rookies.assetmanagement.dto.request.Asset.AssetRequestDTO;
import com.nashtech.rookies.assetmanagement.dto.request.Asset.CreateAssetRequest;
import com.nashtech.rookies.assetmanagement.dto.request.Asset.EditAssetRequest;
import com.nashtech.rookies.assetmanagement.dto.response.AssetResponseDto;
import com.nashtech.rookies.assetmanagement.dto.response.ResponseDto;
import com.nashtech.rookies.assetmanagement.service.AssetService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 *
 * @author HP
 * @author Tamina
 */
@RestController
@RequestMapping("/api/v1/assets")
@AllArgsConstructor
public class AssetController {

    private final AssetService assetService;

    @GetMapping
    public ResponseEntity getAll(@Valid AssetRequestDTO requestParams, Pageable pageable, Authentication authentication) {
        UserDetailsDto requestUser = (UserDetailsDto) authentication.getPrincipal();
        ResponseDto assets = assetService.getAll(requestParams, pageable, requestUser);
        return ResponseEntity.ok().body(assets);
    }

    @GetMapping("/")
    public ResponseEntity getAll() {
        ResponseDto assets = assetService.getAll();
        return ResponseEntity.ok().body(assets);
    }

    @GetMapping("/{assetCode}")
    public ResponseEntity getOne(@PathVariable("assetCode") String assetCode) {
        ResponseDto assets = assetService.getOne(assetCode);
        return ResponseEntity.ok().body(assets);
    }

    @PostMapping()
    public ResponseEntity<ResponseDto<AssetResponseDto>> createAsset(@RequestBody CreateAssetRequest request, Authentication authentication) {
        UserDetailsDto requestUser = (UserDetailsDto) authentication.getPrincipal();
        return ResponseEntity.status(HttpStatus.CREATED).body(assetService.saveAsset(request, requestUser));
    }

    @PatchMapping("/{assetCode}")
    public ResponseEntity<ResponseDto<AssetResponseDto>> editAsset(@PathVariable("assetCode") String assetCode, @RequestBody EditAssetRequest request) {
        return ResponseEntity.status(HttpStatus.OK).body(assetService.editAsset(assetCode, request));
    }
}
