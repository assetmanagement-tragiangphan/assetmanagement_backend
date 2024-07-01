/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.nashtech.rookies.assetmanagement.dto.request.Asset;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author HP
 * @author Tamina
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AssetRequestDTO {

    private String search;
    private List<String> states;
    private List<Long> categories;
}
