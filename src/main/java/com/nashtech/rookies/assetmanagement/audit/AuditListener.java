package com.nashtech.rookies.assetmanagement.audit;

import com.nashtech.rookies.assetmanagement.entity.AuditMetadata;
import com.nashtech.rookies.assetmanagement.entity.User;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;

import java.time.LocalDateTime;

public class AuditListener {

    @PrePersist
    public void setCreatedOn(Auditable auditable) {
        AuditMetadata audit = auditable.getAuditMetadata();

        if(audit == null) {
            audit = new AuditMetadata();
            auditable.setAuditMetadata(audit);
        }
        //TODO: depending on Spring Security implementation
        audit.setCreatedOn(LocalDateTime.now());
        audit.setCreatedBy(User.builder().id(1).build());
    }

    @PreUpdate
    public void setUpdatedOn(Auditable auditable) {
        AuditMetadata audit = auditable.getAuditMetadata();

        //TODO: depending on Spring Security implementation
        audit.setUpdatedOn(LocalDateTime.now());
        audit.setUpdatedBy(User.builder().id(1).build());
    }

}
