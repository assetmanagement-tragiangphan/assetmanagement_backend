/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.nashtech.rookies.assetmanagement.dto.response;

import org.springframework.data.domain.Page;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 *
 * @author HP
 * @author Tamina
 */
@Data
@AllArgsConstructor
public class AssetWithHistoryResponseDTO {

    private AssetResponseDto asset;
    private Page<AssetHistoryDTO> history;

}
