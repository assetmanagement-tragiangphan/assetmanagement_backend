package com.nashtech.rookies.assetmanagement.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.nashtech.rookies.assetmanagement.audit.AuditListener;
import com.nashtech.rookies.assetmanagement.audit.Auditable;

import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Getter
@Setter
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
    @JsonBackReference
    private ReturnRequest returnRequest;

//    @Override
//    public String toString() {
//        return "Assignment{" + "assignedDate=" + assignedDate + ", note=" + note + ", asset=" + asset + ", assignee=" + assignee + ", returnRequest=" + returnRequest + '}';
//    }
    
    
    
}