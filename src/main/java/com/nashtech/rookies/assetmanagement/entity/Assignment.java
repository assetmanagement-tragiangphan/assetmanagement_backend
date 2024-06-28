package com.nashtech.rookies.assetmanagement.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.nashtech.rookies.assetmanagement.audit.AuditListener;
import com.nashtech.rookies.assetmanagement.audit.Auditable;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Data
@Entity
@EntityListeners(AuditListener.class)
public class Assignment extends BaseEntity implements Auditable {
    
    private LocalDate assignedDate;
    private String note;

    @Embedded
    private AuditMetadata auditMetadata;

    @ManyToOne
    @JoinColumn(name = "asset_id")
    @JsonManagedReference
    private Asset asset;
    
    @ManyToOne
    @JoinColumn(name = "assignee_id")
    private User assignee;
    
    @OneToOne(mappedBy = "assignment")
    private ReturnRequest returnRequest;

    @Override
    public String toString() {
        return "Assignment{" + "assignedDate=" + assignedDate + ", note=" + note + ", asset=" + asset + ", assignee=" + assignee + ", returnRequest=" + returnRequest + '}';
    }
    
    
    
}