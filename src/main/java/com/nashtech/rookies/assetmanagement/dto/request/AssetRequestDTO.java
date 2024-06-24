/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.nashtech.rookies.assetmanagement.dto.request;

import java.util.List;
import lombok.Data;

/**
 *
 * @author HP
 * @author Tamina
 */
@Data
public class AssetRequestDTO {

    private String search;
    private List<Long> states;
    private List<Long> categories;
}
