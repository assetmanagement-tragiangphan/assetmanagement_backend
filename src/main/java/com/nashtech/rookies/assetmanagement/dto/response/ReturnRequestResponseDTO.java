/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package com.nashtech.rookies.assetmanagement.dto.response;

import com.nashtech.rookies.assetmanagement.util.StatusConstant;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 *
 * @author HP
 * @author Tamina
 */
@Data
@AllArgsConstructor
public class ReturnRequestResponseDTO {
    private String assetCode;
    private String assetName;
    private String requestedBy;
    private String assignedDate;
    private String acceptedBy;
    private String returnedDate;
    private StatusConstant state;
}
