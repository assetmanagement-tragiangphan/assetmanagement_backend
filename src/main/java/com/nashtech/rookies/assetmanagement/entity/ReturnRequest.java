package com.nashtech.rookies.assetmanagement.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.nashtech.rookies.assetmanagement.audit.AuditListener;
import com.nashtech.rookies.assetmanagement.audit.Auditable;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
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
public class ReturnRequest extends BaseEntity implements Auditable {

    private LocalDate returnedDate;

    @ManyToOne
    @JoinColumn(name = "acceptedBy")
    private User acceptedBy;

    @ManyToOne
    @JoinColumn(name = "requestedBy")
    private User requestedBy;

    @OneToOne
    @JoinColumn(name = "assignmentId")
    @JsonManagedReference
    private Assignment assignment;

    @Embedded
    private AuditMetadata auditMetadata;

    @Override
    public String toString() {
//        return "ReturnRequest{" + "returnedDate=" + returnedDate + ", acceptedBy=" + acceptedBy.getUsername() + ", assignment=" + assignment.getId() + '}';
        return "ReturnRequest{" + "returnedDate=" + returnedDate + ", assignment=" + assignment.getId() + '}';
    }
    
    
}
