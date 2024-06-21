package com.nashtech.rookies.assetmanagement.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.logging.log4j.util.Lazy;

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
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "createdBy", updatable = false)
    private User createdBy;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "updatedBy", insertable = false)
    private User updatedBy;
}
