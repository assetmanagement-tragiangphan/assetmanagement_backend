package com.nashtech.rookies.assetmanagement.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Embeddable
public class AuditMetadata {
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdOn;
    @Column(insertable = false)
    private LocalDateTime updatedOn;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "createdBy", updatable = false)
    private User createdBy;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "updatedBy", insertable = false)
    private User updatedBy;

    @Override
    public String toString() {
        return "AuditMetadata{" + "createdOn=" + createdOn + ", updatedOn=" + updatedOn + '}';
    }
    
    
}
