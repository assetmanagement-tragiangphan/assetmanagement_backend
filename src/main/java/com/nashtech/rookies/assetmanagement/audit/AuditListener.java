package com.nashtech.rookies.assetmanagement.audit;

import com.nashtech.rookies.assetmanagement.dto.UserDetailsDto;
import com.nashtech.rookies.assetmanagement.entity.AuditMetadata;
import com.nashtech.rookies.assetmanagement.entity.User;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;

public class AuditListener {

    @PrePersist
    public void setCreatedOn(Auditable auditable) {
        AuditMetadata audit = auditable.getAuditMetadata();

        if(audit == null) {
            audit = new AuditMetadata();
            auditable.setAuditMetadata(audit);
        }

        var creator = (UserDetailsDto) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        audit.setCreatedOn(LocalDateTime.now());
        audit.setCreatedBy(User.builder().id(creator.getId()).build());
    }

    @PreUpdate
    public void setUpdatedOn(Auditable auditable) {
        AuditMetadata audit = auditable.getAuditMetadata();

        var updater = (UserDetailsDto) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        audit.setUpdatedOn(LocalDateTime.now());
        audit.setUpdatedBy(User.builder().id(updater.getId()).build());
    }

}
