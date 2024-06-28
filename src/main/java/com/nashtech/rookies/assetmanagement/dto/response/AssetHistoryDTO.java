/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.nashtech.rookies.assetmanagement.dto.response;

import java.time.LocalDate;
import lombok.Data;

/**
 *
 * @author HP
 * @author Tamina
 */
@Data
public class AssetHistoryDTO {

    public AssetHistoryDTO(LocalDate date, String assignedTo, String assignedBy, LocalDate returnedDate) {
        this.date = date;
        this.assignedTo = assignedTo;
        this.assignedBy = assignedBy;
        this.returnedDate = returnedDate;
    }

    private LocalDate date;
    private String assignedTo;
    private String assignedBy;
    private LocalDate returnedDate;
}
