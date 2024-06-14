package com.nashtech.rookies.assetmanagement.audit;

import com.nashtech.rookies.assetmanagement.entity.AuditMetadata;

public interface Auditable {
    AuditMetadata getAuditMetadata();
    void setAuditMetadata(AuditMetadata auditMetadata);
}
