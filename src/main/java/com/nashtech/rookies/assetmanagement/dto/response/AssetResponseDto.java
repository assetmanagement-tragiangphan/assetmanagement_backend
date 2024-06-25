/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.nashtech.rookies.assetmanagement.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author HP
 * @author Tamina
 */
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AssetResponseDto {
    private String assetCode;
    private String name;
    private String category;
    private String state;
//    private String installedDate;
//    private String location;
//    private String specification;
}
