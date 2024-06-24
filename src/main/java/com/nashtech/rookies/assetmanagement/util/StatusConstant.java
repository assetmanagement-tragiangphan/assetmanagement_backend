package com.nashtech.rookies.assetmanagement.util;

public enum StatusConstant {
    // All
    ACTIVE, INACTIVE,

    // Asset
    AVAILABLE, NOT_AVAILABLE, ASSIGNED, WAITING_FOR_RECYCLING, RECYCLED,
    
    // Assignment
    ACCEPTED, WAITING_FOR_ACCEPTANCE, DECLINED,
    
    // Return Request
    COMPLETED, WAITING_FOR_RETURNING
}
