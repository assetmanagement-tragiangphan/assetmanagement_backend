package com.nashtech.rookies.assetmanagement.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Embeddable
public class AuditMetadata {
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdOn;
    @Column(insertable = false)
    private LocalDateTime updatedOn;
    @ManyToOne
    @JoinColumn(name = "createdBy", updatable = false)
    private User createdBy;
    @ManyToOne
    @JoinColumn(name = "updatedBy", insertable = false)
    private User updatedBy;
}
